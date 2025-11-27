package com.smartparking.Smartparking.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfileResponseDto {
    private String firstName;
    private String lastName;
}