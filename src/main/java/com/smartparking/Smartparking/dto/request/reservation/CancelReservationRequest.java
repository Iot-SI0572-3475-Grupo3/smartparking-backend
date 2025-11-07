package com.smartparking.Smartparking.dto.request.reservation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelReservationRequest {
    @NotBlank(message = "La razón de cancelación es obligatoria")
    @Size(max = 500, message = "La razón no puede exceder 500 caracteres")
    private String reason;
}