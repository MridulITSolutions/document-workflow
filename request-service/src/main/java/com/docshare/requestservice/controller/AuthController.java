package com.docshare.requestservice.controller;

import com.docshare.requestservice.dto.LoginRequest;
import com.docshare.requestservice.dto.LoginResponse;
import com.docshare.requestservice.service.AuthenticationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {

        return authenticationService.login(request);

    }
}