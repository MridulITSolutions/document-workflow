package com.docshare.requestservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateRequestResponse {

    private Long requestId;

    private String status;

    private String message;

}