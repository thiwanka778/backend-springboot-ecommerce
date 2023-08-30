package com.alibou.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenExtractor {

    @Autowired
     JwtService jwtService;



    public String extractUserEmail(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove the "Bearer " prefix
            return jwtService.extractUsername(token);
        }
        return null; // Return null if token is missing or invalid
    }
}
