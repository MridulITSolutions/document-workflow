package com.docshare.requestservice.service;

import com.docshare.common.entity.RequestMaster;
import com.docshare.common.entity.RequestProgress;
import com.docshare.common.entity.UserMaster;
import com.docshare.common.repository.RequestProgressRepository;
import com.docshare.common.repository.RequestRepository;
import com.docshare.common.repository.UserRepository;
import com.docshare.requestservice.dto.WorkflowRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkflowService {
    private final RequestRepository requestRepository;
    private final RequestProgressRepository progressRepository;
    private final UserRepository userRepository;

    public WorkflowService(RequestRepository requestRepository,
                           RequestProgressRepository progressRepository,
                           UserRepository userRepository) {

        this.requestRepository = requestRepository;
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
    }

    public void startWorkflow(WorkflowRequest request) {

        RequestMaster requestMaster =
                requestRepository.findById(request.getRequestId()).orElseThrow();

        UserMaster requestor =
                userRepository.findById(requestMaster.getRequestedById()).orElseThrow();
        //Step 0 Mark the request submitted stage completed
        RequestProgress submittedProgress = new RequestProgress();

        submittedProgress.setRequestId(requestMaster.getRequestId());
        submittedProgress.setStage("SUBMITTED");
        submittedProgress.setApproverId(requestMaster.getRequestedById());
        submittedProgress.setStatus("COMPLETED");
        submittedProgress.setComments("Request Submitted");
        submittedProgress.setCreatedDate(LocalDateTime.now());
        submittedProgress.setModifiedDate(LocalDateTime.now());

        progressRepository.save(submittedProgress);

        //Step 1 get the first approver id from workflow- Mostly manager id
        List<String> workflow = buildWorkflow(request.getConfidentiality());
        String stage = workflow.get(0);

        Long approverId = switch (stage.toUpperCase()) {

            case "MANAGER" -> requestor.getManagerId();

            case "SM" -> requestor.getSmId();

            case "SECURITY" -> requestor.getSecurityId();

            default -> throw new IllegalArgumentException("Invalid Stage");
        };
        //Step 2 Update Workflow Progress in DB with approver stage fetched above as pending
        RequestProgress progress = new RequestProgress();

        progress.setRequestId(requestMaster.getRequestId());
        progress.setStage(stage);
        progress.setApproverId(approverId);
        progress.setStatus("PENDING");
        progress.setCreatedDate(LocalDateTime.now());
        progress.setModifiedDate(LocalDateTime.now());

        progressRepository.save(progress);
       //Step 3 Update current approver id in DB across request
        requestMaster.setCurrentApproverId(approverId);
        //requestMaster.setStatus("IN_PROGRESS");
        requestMaster.setModifiedDate(LocalDateTime.now());

        requestRepository.save(requestMaster);
        //Step 4 Send Notification Email to approver
        /**No Email in Local. In AWS Prod. Step Function will invoke Lambda to Send email*/

        System.out.println("Workflow Completed");

    }
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