package com.smartparking.Smartparking.dto.response;

import com.smartparking.Smartparking.entity.iam.User.Status;
import lombok.Data;

@Data
public class LoginResponseDto {
    private String token;
    private String sessionId;
    private Status status;
}