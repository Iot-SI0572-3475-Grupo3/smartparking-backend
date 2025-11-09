package com.smartparking.Smartparking.controller.space_iot;

import com.smartparking.Smartparking.dto.response.reservation.ReservationResponse;
import com.smartparking.Smartparking.entity.reservation.Reservation;
import com.smartparking.Smartparking.mapper.reservation.ReservationMapper;
import com.smartparking.Smartparking.service.reservation.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/iot/reservation")
@RequiredArgsConstructor
class SpaceIotController {
    private final ReservationService reservationService;
    private final ReservationMapper reservationMapper;

    @PostMapping("/activate/{spaceId}")
    @PreAuthorize("hasRole('IOT')")
    public ResponseEntity<ReservationResponse> activateReservation(@PathVariable String spaceId) {
        Reservation activated = reservationService.activateReservationBySpace(spaceId);
        return ResponseEntity.ok(reservationMapper.toResponse(activated));
    }
}
