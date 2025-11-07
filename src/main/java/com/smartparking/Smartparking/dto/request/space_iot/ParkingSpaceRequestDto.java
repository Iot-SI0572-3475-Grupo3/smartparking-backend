package com.smartparking.Smartparking.dto.request.space_iot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpaceRequestDto {

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 10, message = "El código no puede exceder 10 caracteres")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "El código solo puede contener letras mayúsculas y números")
    private String code;

    private String status;
}