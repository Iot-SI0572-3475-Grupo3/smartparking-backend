package com.smartparking.Smartparking.dto.request;

import com.smartparking.Smartparking.entity.iam.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data

public class UserRequestDto {
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String passwordHash;

    @NotNull(message = "Role is required")
    private User.Role role;

    @NotNull(message = "Status is required")
    private User.Status status;
}