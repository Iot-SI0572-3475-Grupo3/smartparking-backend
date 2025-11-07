package com.smartparking.Smartparking.dto.response.reservation;

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
public class ReservationHistoryResponse {
    private String reservationId;
    private String spaceCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime date;
    private String status; // "completed", "cancelled", "expired"
    private String vehicleInfo;
    private String specialRequirements;
    private BigDecimal totalCost;
    private LocalDateTime completedAt;
    private LocalDateTime cancelledAt;
    private String cancellationReason; // solo para cancelled
}