package com.smartparking.Smartparking.dto.response.reservation;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponse {
    private String reservationId;
    private String spaceCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime date;
    private String status;
    private String vehicleInfo;
    private String specialRequirements;
    private BigDecimal totalCost;
    private String paymentStatus;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime cancelledAt;
    private LocalDateTime completedAt;
    private String cancellationReason;
}