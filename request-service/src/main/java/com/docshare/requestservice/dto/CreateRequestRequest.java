package com.docshare.requestservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateRequestRequest {

    private String requestName;

    private String destinationName;

    private String documentName;

    private String documentType;

    private String attachmentPath;

    private String confidentiality;

    private String reason;

    private String comments;

}