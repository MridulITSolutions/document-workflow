package com.docshare.requestservice.service;

import com.docshare.common.entity.UserMaster;
import com.docshare.common.repository.UserRepository;
import com.docshare.requestservice.dto.LoginRequest;
import com.docshare.requestservice.dto.LoginResponse;
import com.docshare.requestservice.security.JwtService;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;


    public AuthenticationService(UserRepository userRepository,JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        UserMaster user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid Email"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid Password");
        }
        String token = jwtService.generateToken(user);
        return LoginResponse.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .department(user.getDepartment())
                .role(user.getRole())
                .status("SUCCESS")
                .token(token)
                .build();
    }
}