package com.docshare.common.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CurrentUser {

    private Long userId;
    private String userName;
    private String role;
    private String email;
    private String department;

}