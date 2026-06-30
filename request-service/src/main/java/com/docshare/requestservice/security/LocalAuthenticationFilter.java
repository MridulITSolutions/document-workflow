package com.docshare.requestservice.security;

import com.docshare.common.dto.CurrentUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Profile("local")
public class LocalAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public LocalAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if(token != null && token.startsWith("Bearer ")) {

            CurrentUser user =
                    jwtService.parse(token.substring(7));

            LocalCurrentUserProvider.set(user);
        }

        chain.doFilter(request,response);
    }
}