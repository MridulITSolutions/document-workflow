package com.docshare.requestservice.service;

import com.docshare.requestservice.dto.WorkflowRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sfn.SfnClient;
import software.amazon.awssdk.services.sfn.model.StartExecutionRequest;
import software.amazon.awssdk.services.sfn.model.StartExecutionResponse;

@Component
@Profile({"dev","prod"})
public class AwsWorkflowInitiator implements WorkflowInitiator {

    private final SfnClient sfnClient;
    private final ObjectMapper objectMapper;

    @Value("${spring.aws.stepfunctions.stateMachineArn}")
    private String stateMachineArn;

    public AwsWorkflowInitiator(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;

        // FIX: Updated from US_EAST_1 to AP_SOUTH_1 to match your Mumbai infrastructure
        this.sfnClient = SfnClient.builder()
                .region(Region.AP_SOUTH_1)
                .build();
    }

    @Override
    public void initiate(WorkflowRequest request) {
        try {
            // 1. Serialize our Java WorkflowRequest DTO directly into a JSON string payload
            String jsonInput = objectMapper.writeValueAsString(request);

            // 2. Build the execution request targeting our specific State Machine instance
            StartExecutionRequest executionRequest = StartExecutionRequest.builder()
                    .stateMachineArn(stateMachineArn)
                    // Sanitize the name string (AWS limits characters allowed in execution names to alphanumeric, hyphens, and underscores)
                    .name("Request-" + request.getRequestId() + "-" + System.currentTimeMillis())
                    .input(jsonInput)
                    .build();

            System.out.println("Initiating AWS Step Function execution for Request ID: " + request.getRequestId());

            // 3. Trigger the Step Function execution state machine
            StartExecutionResponse response = sfnClient.startExecution(executionRequest);

            System.out.println("Step Function started successfully. Execution ARN: " + response.executionArn());

        } catch (Exception e) {
            System.err.println("CRITICAL: Failed to kick off AWS Step Function pipeline for Request ID: " + request.getRequestId());
            e.printStackTrace();
        }
    }
}