package com.docshare.requestservice.controller;

import com.docshare.requestservice.dto.WorkflowRequest;
import com.docshare.requestservice.service.WorkflowService;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.context.annotation.Profile;
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
    public void initiateWorkflow(
            @RequestBody WorkflowRequest request) {

        workflowService.startWorkflow(request);

    }

}