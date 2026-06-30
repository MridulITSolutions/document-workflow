package com.docshare.common.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "request_master", schema = "docshare")
@Getter
@Setter
public class RequestMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long requestId;

    @Column(name = "request_name")
    private String requestName;

    @Column(name = "requested_by_id")
    private Long requestedById;

    @Column(name = "destination_name")
    private String destinationName;

    @Column(name = "document_name")
    private String documentName;

    @Column(name = "document_type")
    private String documentType;

    @Column(name = "attachment_path")
    private String attachmentPath;

    @Column(name = "confidentiality")
    private String confidentiality;

    @Column(name = "reason")
    private String reason;

    @Column(name = "comments")
    private String comments;

    @Column(name = "status")
    private String status;

    @Column(name = "current_approver_id")
    private Long currentApproverId;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

}