package com.docshare.requestservice.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {

    private Long userId;
    private String userName;
    private String email;
    private String role;
    private String department;
    private String token;
    private String status;

}