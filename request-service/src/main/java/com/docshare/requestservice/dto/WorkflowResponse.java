package com.docshare.requestservice.dto;

import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
public class WorkflowResponse {
    private String approverEmail;
    private String requestName;
    private String appUrl;
    private String status;
}
