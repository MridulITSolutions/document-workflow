package com.docshare.requestservice.security;

import com.docshare.common.dto.CurrentUser;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev","prod"})
public class AwsAuthenticationFilter extends AbstractAuthenticationFilter {

    private final JwtService jwtService;

    public AwsAuthenticationFilter(JwtService jwtService) {
        //TO-DO replace with Cognito Service
        this.jwtService = jwtService;
    }

    @Override
    protected CurrentUser parseToken(String token) {

        // TO-DO Later replace with cognitoJwtService.parse(token);
        return jwtService.parse(token);

    }

    @Override
    protected void setCurrentUser(CurrentUser user) {

        AwsCurrentUserProvider.set(user);

    }
}