package com.docshare.requestservice.controller;

import com.docshare.requestservice.dto.WorkflowRequest;
import com.docshare.requestservice.dto.WorkflowResponse;
import com.docshare.requestservice.service.WorkflowService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Hidden
@RestController
@RequestMapping("/internal")
public class WorkflowController {

    private final WorkflowService workflowService;

    public WorkflowController(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    @PostMapping("/workflow")
    public ResponseEntity<WorkflowResponse> initiateWorkflow(
            @RequestBody WorkflowRequest request) {

        WorkflowResponse response = workflowService.startWorkflow(request);
        return ResponseEntity.ok(response);
    }

}