package com.docshare.requestservice.service;

import com.docshare.requestservice.dto.WorkflowRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"dev","prod"})
public class AwsWorkflowInitiator implements WorkflowInitiator {

    @Override
    public void initiate(WorkflowRequest request) {

        // TODO
        // Start Step Function
        // Pass WorkflowRequest as execution input

        System.out.println("Starting Step Function");

    }
}