package com.docshare.requestservice.security;

import com.docshare.common.dto.CurrentUser;
import com.docshare.common.entity.UserMaster;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;

import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    private static final String SECRET =
            "docshare-local-secret-key-docshare-local-secret";

    private final SecretKey key =
            Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    public String generateToken(UserMaster user) {

        return Jwts.builder()
                .subject(user.getEmail())
                .claim("userId", user.getUserId())
                .claim("userName", user.getUserName())
                .claim("department", user.getDepartment())
                .claim("role", user.getRole())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }
    public CurrentUser parse(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return CurrentUser.builder()
                .userId(claims.get("userId", Long.class))
                .userName(claims.get("userName", String.class))
                .email(claims.getSubject())
                .department(claims.get("department", String.class))
                .role(claims.get("role", String.class))
                .build();
    }
}