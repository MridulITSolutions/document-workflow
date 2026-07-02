package com.docshare.requestservice.security;

import com.docshare.common.dto.CurrentUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public abstract class AbstractAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {

        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {

            CurrentUser user =
                    parseToken(token.substring(7));

            if (user != null) {
                setCurrentUser(user);
            }
        }

        chain.doFilter(request, response);
    }

    protected abstract CurrentUser parseToken(String token);

    protected abstract void setCurrentUser(CurrentUser user);
}