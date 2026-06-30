package com.docshare.requestservice.security;

import com.docshare.common.dto.CurrentUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
/** invoked for DEV/QE/PROD set up and AWS Cognito based authentication **/
@Profile({"dev","prod"})
public class AwsCurrentUserProvider implements CurrentUserProvider {

    private final HttpServletRequest request;

    public AwsCurrentUserProvider(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public CurrentUser getCurrentUser() {
        /** API Gateway/Lambda authorizer will populate these headers **/
        return CurrentUser.builder()
                .userId(Long.valueOf(request.getHeader("X-USER-ID")))
                .userName(request.getHeader("X-USER-NAME"))
                .email(request.getHeader("X-USER-EMAIL"))
                .department(request.getHeader("X-DEPARTMENT"))
                .build();
    }
}