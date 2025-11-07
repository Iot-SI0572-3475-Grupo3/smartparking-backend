package com.smartparking.Smartparking.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileRequestDto {
    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;
}