package com.smartparking.Smartparking.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserProfileResponseDto {
    private String profileId;
    private String userId;
    private String firstName;
    private String lastName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}