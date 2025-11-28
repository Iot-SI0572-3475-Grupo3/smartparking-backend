package com.smartparking.Smartparking.controller.reservation;

import com.smartparking.Smartparking.dto.request.reservation.CancelReservationRequest;
import com.smartparking.Smartparking.dto.request.reservation.ReservationRequestDto;
import com.smartparking.Smartparking.dto.response.reservation.ActiveReservationResponse;
import com.smartparking.Smartparking.dto.response.reservation.ReservationHistoryResponse;
import com.smartparking.Smartparking.dto.response.reservation.ReservationResponse;
import com.smartparking.Smartparking.entity.reservation.Reservation;
import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import com.smartparking.Smartparking.repository.reservation.ReservationRepository;
import com.smartparking.Smartparking.repository.space_iot.ParkingSpaceRepository;
import com.smartparking.Smartparking.service.reservation.ReservationService;
import com.smartparking.Smartparking.service.space_iot.ParkingSpaceService;
import io.jsonwebtoken.Jwt;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    private final ParkingSpaceService parkingSpaceService;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ReservationRepository reservationRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody ReservationRequestDto request) {

        ReservationResponse response = reservationService.createReservation(request);
        return ResponseEntity.status(201).body(response);
    }

    private String getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof String userId) {
            return userId;
        }
        throw new IllegalStateException("User not authenticated");
    }

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservationHistoryResponse>> getReservationHistory() {

        String userId = getCurrentUserId();

        List<ReservationHistoryResponse> history =
                reservationService.getReservationHistory(userId);

        return ResponseEntity.ok(history);
    }

    @GetMapping("/active")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ActiveReservationResponse> getActiveReservation() {

        String userId = getCurrentUserId();

        return reservationService.getActiveReservation(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build()); // 204 si no hay activa
    }

    @PostMapping("/{reservationId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponse> cancelReservation(
            @PathVariable String reservationId,
            @Valid @RequestBody CancelReservationRequest request) {

        String userId = getCurrentUserId();
        Reservation cancelled = reservationService.cancelReservation(reservationId, userId, request);

        return ResponseEntity.ok(mapToResponse(cancelled));
    }

    private ReservationResponse mapToResponse(Reservation res) {
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

    @PostMapping("/{reservationId}/confirm")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponse> confirmReservation(
            @PathVariable String reservationId) {

        String userId = getCurrentUserId();
        Reservation confirmed = reservationService.confirmReservation(reservationId, userId);

        return ResponseEntity.ok(mapToResponse(confirmed));
    }

    @PostMapping("/{reservationId}/activate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ReservationResponse> activateReservation(@PathVariable String reservationId) {
        Reservation activated = reservationService.activateReservation(reservationId);
        return ResponseEntity.ok(mapToResponse(activated));
    }

    @PostMapping("/{reservationId}/complete")
    @PreAuthorize("isAuthenticated()")
    public Reservation completeReservation(@PathVariable String reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        if (res.getStatus() != Reservation.ReservationStatus.active) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La reserva no está activa");
        }

        res.setStatus(Reservation.ReservationStatus.completed);
        res.setCompletedAt(LocalDateTime.now());
        res.setEndTime(LocalDateTime.now());

        // Calcular costo
        long minutes = Duration.between(res.getStartTime(), LocalDateTime.now()).toMinutes();
        BigDecimal cost = BigDecimal.valueOf(minutes).multiply(new BigDecimal("0.05"));
        res.setTotalCost(cost);
        res.setPaymentStatus(Reservation.PaymentStatus.paid);

        // Liberar espacio
        ParkingSpace space = res.getParkingSpace();
        space.setStatus(ParkingSpace.SpaceStatus.available);
        space.setCurrentReservationId(null);
        parkingSpaceRepository.save(space);

        return reservationRepository.save(res);
    }

    @PostMapping("/{reservationId}/expire")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> expireReservation(@PathVariable String reservationId) {
        reservationService.expireReservationManually(reservationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/parking-spaces-reservations/{code}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ReservationResponse>> getReservationsByCode(
            @PathVariable String code) {

        List<ReservationResponse> reservations =
                reservationService.getReservationsByParkingSpaceCode(code);

        return ResponseEntity.ok(reservations);
    }
}