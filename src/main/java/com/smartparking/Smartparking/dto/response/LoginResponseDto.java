package com.smartparking.Smartparking.dto.response;

import lombok.Data;

@Data
public class LoginResponseDto {
    private String token;
    private String sessionId;
}