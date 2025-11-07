package com.smartparking.Smartparking.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserSessionResponseDto {
    private String sessionId;
    private String userId;
    private String tokenHash;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private Boolean isActive;
}