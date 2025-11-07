package com.smartparking.Smartparking.dto.request.reservation;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRequestDto {

    @NotBlank
    private String spaceId;

    @NotNull
    @FutureOrPresent
    private LocalDateTime startTime;

    private String vehicleInfo; // String con JSON

    private String specialRequirements; // Texto plano
}