package com.docshare.requestservice.service;

import com.docshare.common.dto.CurrentUser;
import com.docshare.common.entity.RequestMaster;
import com.docshare.common.entity.UserMaster;
import com.docshare.common.repository.RequestRepository;
import com.docshare.common.repository.UserRepository;
import com.docshare.requestservice.dto.CreateRequestRequest;
import com.docshare.requestservice.dto.CreateRequestResponse;
import com.docshare.requestservice.dto.RequestSummaryResponse;
import com.docshare.requestservice.dto.WorkflowRequest;
import com.docshare.requestservice.security.CurrentUserProvider;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final CurrentUserProvider currentUserProvider;
    private final WorkflowInitiator workflowInitiator;


    public RequestService(RequestRepository requestRepository,
                          UserRepository userRepository,
                          CurrentUserProvider currentUserProvider,
                          WorkflowInitiator workflowInitiator) {

        this.requestRepository = requestRepository;
        this.userRepository    = userRepository;
        this.currentUserProvider = currentUserProvider;
        this.workflowInitiator = workflowInitiator;
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
}