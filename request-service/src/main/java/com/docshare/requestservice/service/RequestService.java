package com.docshare.requestservice.service;

import com.docshare.common.dto.CurrentUser;
import com.docshare.common.entity.RequestMaster;
import com.docshare.common.entity.RequestProgress;
import com.docshare.common.entity.UserMaster;
import com.docshare.common.repository.RequestProgressRepository;
import com.docshare.common.repository.RequestRepository;
import com.docshare.common.repository.UserRepository;
import com.docshare.requestservice.dto.*;
import com.docshare.requestservice.security.CurrentUserProvider;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final WorkflowInitiator workflowInitiator;
    private final RequestProgressRepository requestProgressRepository;


    public RequestService(RequestRepository requestRepository,
                          UserRepository userRepository,
                          CurrentUserProvider currentUserProvider,
                          WorkflowInitiator workflowInitiator,
                          RequestProgressRepository requestProgressRepository) {

        this.requestRepository = requestRepository;
        this.userRepository    = userRepository;
        this.currentUserProvider = currentUserProvider;
        this.workflowInitiator = workflowInitiator;
        this.requestProgressRepository= requestProgressRepository;
    }

    public CreateRequestResponse createRequest(CreateRequestRequest request) {

        CurrentUser user = currentUserProvider.getCurrentUser();

        RequestMaster entity = new RequestMaster();

        entity.setRequestName(request.getRequestName());
        entity.setRequestedById(user.getUserId());
        entity.setDestinationName(request.getDestinationName());
        entity.setDocumentName(request.getDocumentName());
        entity.setDocumentType(request.getDocumentType());
        entity.setAttachmentPath(request.getAttachmentPath());
        entity.setConfidentiality(request.getConfidentiality());
        entity.setReason(request.getReason());
        entity.setComments(request.getComments());

        entity.setStatus("Pending Approval");
        entity.setCreatedDate(LocalDateTime.now());
        entity.setModifiedDate(LocalDateTime.now());

        entity = requestRepository.save(entity);

       // List<String> workflow = buildWorkflow(request.getConfidentiality());
        WorkflowRequest workflowRequest = new WorkflowRequest();

        workflowRequest.setRequestId(entity.getRequestId());
        workflowRequest.setConfidentiality(request.getConfidentiality());
        //workflowOrchestrator.startWorkflow(workflowRequest);

        workflowInitiator.initiate(workflowRequest);

        return CreateRequestResponse.builder()
                .requestId(entity.getRequestId())
                .status("Pending Approval")
                .message("Request Created Successfully")
                .build();

    }
    public List<RequestSummaryResponse> getRequests() {

        CurrentUser user = currentUserProvider.getCurrentUser();

        List<RequestMaster> requests;

        if ("ADMIN".equalsIgnoreCase(user.getRole())) {

            requests = requestRepository.findAll();

        } else {

            requests = requestRepository.findByRequestedById(user.getUserId());

        }

        return requests.stream()
                .map(request -> {

                    UserMaster requestor = userRepository
                            .findById(request.getRequestedById())
                            .orElseThrow();

                    return RequestSummaryResponse.builder()
                            .requestId(request.getRequestId())
                            .requestName(request.getRequestName())
                            .requestedById(requestor.getUserId())
                            .requestedByName(requestor.getUserName())
                            .department(requestor.getDepartment())
                            .destinationName(request.getDestinationName())
                            .documentName(request.getDocumentName())
                            .documentType(request.getDocumentType())
                            .confidentiality(request.getConfidentiality())
                            .status(request.getStatus())
                            .currentApproverId(request.getCurrentApproverId())
                            .createdDate(request.getCreatedDate())
                            .build();
                })
                .toList();
    }

    public RequestDetailResponse getRequestById(Long requestId) {
        // 0. Fetch current authenticated context user metrics
        CurrentUser user = currentUserProvider.getCurrentUser();

        // 1. Pull core request master record
        RequestMaster request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found for ID: " + requestId));
        // role based access check
        if (!"ADMIN".equalsIgnoreCase(user.getRole()) && !request.getRequestedById().equals(user.getUserId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Access Denied: You do not have permission to view this request.");
        }
        // 2. Fetch the metadata associated with the creator
        UserMaster requestor = userRepository.findById(request.getRequestedById())
                .orElseThrow(() -> new RuntimeException("Creator user not found"));

        List<String> expectedWorkflow= buildWorkflow(request.getConfidentiality());

        // 3. Collect tracking workflow milestones
        List<RequestProgress> progressList = requestProgressRepository.findByRequestId(requestId);

        // Map every single historical row out of the request_progress table
        List<RequestDetailResponse.AuditTrailStep> auditTrail = progressList.stream()
                .map(progress -> {
                    String name = "System Automation";
                    if (progress.getApproverId() != null) {
                        name = userRepository.findById(progress.getApproverId())
                                .map(UserMaster::getUserName)
                                .orElse("Unknown User");
                    }

                    return RequestDetailResponse.AuditTrailStep.builder()
                            .stage(progress.getStage())
                            .approverId(progress.getApproverId())
                            .approverName(name)
                            .status(progress.getStatus())
                            .comments(progress.getComments())
                            .createdDate(progress.getCreatedDate())
                            .build();
                })
                // Sort chronologically ascending so the oldest actions appear first in the array
                .sorted(java.util.Comparator.comparing(RequestDetailResponse.AuditTrailStep::getCreatedDate))
                .toList();

        // 5. Determine the current active step stage text context ("PENDING" status item)
        String determinedStage = progressList.stream()
                .filter(p -> "PENDING".equalsIgnoreCase(p.getStatus()))
                .map(RequestProgress::getStage)
                .findFirst()
                .orElse("COMPLETED");

        // 6. Build the complete unified UI object response block
        return RequestDetailResponse.builder()
                .requestId(request.getRequestId())
                .requestName(request.getRequestName())
                .requestedById(requestor.getUserId())
                .requestedByName(requestor.getUserName())
                .department(requestor.getDepartment())
                .destinationName(request.getDestinationName())
                .documentName(request.getDocumentName())
                .documentType(request.getDocumentType())
                .attachmentPath(request.getAttachmentPath())
                .confidentiality(request.getConfidentiality())
                .reason(request.getReason())
                .comments(request.getComments())
                .status(request.getStatus())
                .currentApproverId(request.getCurrentApproverId())
                .currentStage(determinedStage)
                .createdDate(request.getCreatedDate())
                .expectedWorkflow(expectedWorkflow)
                .auditTrail(auditTrail)
                .build();
    }
   //TO-DO move to common utility method as this is used by multiple service classes
    private List<String> buildWorkflow(String confidentiality) {

        List<String> workflow = new ArrayList<>();

        if ("RESTRICTED".equalsIgnoreCase(confidentiality)) {

            workflow.add("MANAGER");
            workflow.add("SM");
            workflow.add("SECURITY");

        } else if ("CONFIDENTIAL".equalsIgnoreCase(confidentiality)) {

            workflow.add("MANAGER");
            workflow.add("SM");

        } else {

            workflow.add("MANAGER");

        }

        return workflow;
    }
}