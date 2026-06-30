package com.docshare.requestservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class RequestSummaryResponse {

    private Long requestId;

    private String requestName;

    private String destinationName;

    private String documentName;

    private String documentType;

    private Long requestedById;

    private String requestedByName;

    private String confidentiality;

    private String status;

    private Long currentApproverId;

    private String department;

    private LocalDateTime createdDate;

}