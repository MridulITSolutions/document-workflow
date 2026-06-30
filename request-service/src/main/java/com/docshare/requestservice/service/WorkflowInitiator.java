package com.docshare.requestservice.service;

import com.docshare.requestservice.dto.WorkflowRequest;

public interface WorkflowInitiator {

    void initiate(WorkflowRequest request);

}