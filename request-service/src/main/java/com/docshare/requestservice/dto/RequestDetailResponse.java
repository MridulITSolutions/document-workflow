package com.docshare.requestservice.dto;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class RequestDetailResponse {
    // Request Information Section
    private Long requestId;
    private String requestName;
    private Long requestedById;
    private String requestedByName;
    private String department;
    private String destinationName;
    private String documentName;
    private String documentType;
    private String attachmentPath;
    private String confidentiality;
    private String reason;
    private String comments;
    private String status;
    private Long currentApproverId;
    private String currentStage; // For UI stage tracking highlights
    private LocalDateTime createdDate;

    private List<String> expectedWorkflow;
    private List<AuditTrailStep> auditTrail;

    @Data
    @Builder
    public static class AuditTrailStep {
        private String stage;
        private Long approverId;
        private String approverName;
        private String status;
        private String comments;
        private LocalDateTime createdDate;
    }
}