package com.smartparking.Smartparking.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private String userId;
    private String email;
    private String role;
    private String status;
    private LocalDateTime createdAt;
}