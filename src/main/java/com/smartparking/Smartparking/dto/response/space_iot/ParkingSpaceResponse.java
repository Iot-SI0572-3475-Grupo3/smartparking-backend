package com.smartparking.Smartparking.dto.response.space_iot;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpaceResponse {
    private String spaceId;
    private String code;
    private String status;
    private String currentReservationId;
    private LocalDateTime lastUpdated;
    private LocalDateTime createdAt;
}