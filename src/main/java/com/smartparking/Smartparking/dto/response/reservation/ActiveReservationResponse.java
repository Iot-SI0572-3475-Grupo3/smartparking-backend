package com.smartparking.Smartparking.dto.response.reservation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveReservationResponse {
    private String reservationId;
    private String spaceCode;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String vehicleInfo;
    private String specialRequirements;
    private long minutesUntilArrival;
    private boolean canCancel;
}