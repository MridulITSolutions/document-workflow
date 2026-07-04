package com.docshare.requestservice.security;

import com.docshare.common.dto.CurrentUser;
import com.docshare.common.entity.UserMaster;
import com.docshare.common.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev","prod"})
public class AwsAuthenticationFilter extends AbstractAuthenticationFilter {

    private final JwtService customJwtService;
    private final UserRepository userRepository; // Injecting database access into the filter layer

    public AwsAuthenticationFilter(JwtService customJwtService, UserRepository userRepository) {
        this.customJwtService = customJwtService;
        this.userRepository = userRepository;
    }
    @Override
    protected CurrentUser parseToken(String token) {
        // Strip the Bearer scheme prefix if it is still present
        String cleanToken = token.startsWith("Bearer ") ? token.substring(7) : token;

        try {
            DecodedJWT decoded = JWT.decode(cleanToken);
            String issuer = decoded.getIssuer();

            // AWS COGNITO IDP HANDLER -If Cognito token is passed else support jwt
            if (issuer != null && issuer.contains("cognito-idp")) {
                String email = decoded.getClaim("email").asString();

                // 1. Cross-reference the database using email to get the persistent primary key data
                UserMaster realDbUser = userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Authenticated Cognito user registry entry missing from local DB: " + email));

                // 2. Safely resolve Role and Department strings
                String tokenRole = decoded.getClaim("custom:role").asString();
                String resolvedRole = (tokenRole != null) ? tokenRole.toUpperCase().trim() : realDbUser.getRole();

                String tokenDept = decoded.getClaim("custom:department").asString();
                String resolvedDept = (tokenDept != null) ? tokenDept : realDbUser.getDepartment();

                // 3. Build using clean constructor arguments or builder pattern matching your CurrentUser DTO structure
                // Assuming standard fields: userId, userName, email, department, role
                return CurrentUser.builder()
                        .userId(realDbUser.getUserId())
                        .userName(decoded.getClaim("cognito:username").asString())
                        .email(email)
                        .department(resolvedDept)
                        .role(resolvedRole)
                        .build();
            }
        } catch (Exception e) {
            // Fall through smoothly if Cognito decoding or database fetching hits a structural issue
        }

        // --- ROUTE B: LEGACY BACKEND JWT HANDLER ---
        return customJwtService.parse(cleanToken);
    }

    @Override
    protected void setCurrentUser(CurrentUser user) {
        AwsCurrentUserProvider.set(user);
    }
}