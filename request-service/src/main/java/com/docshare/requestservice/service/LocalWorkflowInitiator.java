package com.docshare.requestservice.service;

import com.docshare.requestservice.dto.WorkflowRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Profile("local")
public class LocalWorkflowInitiator implements WorkflowInitiator {

    private final RestTemplate restTemplate;

    public LocalWorkflowInitiator(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void initiate(WorkflowRequest request) {

        restTemplate.postForObject(
                "http://localhost:9001/internal/workflow",
                new HttpEntity<>(request),
                Void.class);

    }
}