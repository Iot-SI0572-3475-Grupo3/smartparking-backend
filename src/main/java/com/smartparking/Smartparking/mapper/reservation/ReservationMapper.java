package com.smartparking.Smartparking.mapper.reservation;

import com.smartparking.Smartparking.dto.response.reservation.ReservationResponse;
import com.smartparking.Smartparking.entity.reservation.Reservation;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ReservationMapper {

    public ReservationResponse toResponse(Reservation res) {
        return ReservationResponse.builder()
                .reservationId(res.getReservationId())
                .spaceCode(res.getParkingSpace().getCode())
                .startTime(res.getStartTime())
                .endTime(res.getEndTime())
                .date(res.getDate())
                .status(res.getStatus().name().toLowerCase())
                .vehicleInfo(res.getVehicleInfo())
                .specialRequirements(res.getSpecialRequirements())
                .totalCost(res.getTotalCost() != null ? res.getTotalCost() : BigDecimal.ZERO)
                .paymentStatus(res.getPaymentStatus() != null ? res.getPaymentStatus().name().toLowerCase() : null)
                .createdAt(res.getCreatedAt())
                .confirmedAt(res.getConfirmedAt())
                .cancelledAt(res.getCancelledAt())
                .completedAt(res.getCompletedAt())
                .cancellationReason(res.getCancellationReason())
                .build();
    }
}