package com.smartparking.Smartparking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public String getJwtSecret() {
        return jwtSecret;
    }
}