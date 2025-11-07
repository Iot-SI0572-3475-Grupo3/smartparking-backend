package com.smartparking.Smartparking.service.reservation;

import com.smartparking.Smartparking.dto.request.reservation.CancelReservationRequest;
import com.smartparking.Smartparking.dto.request.reservation.ReservationRequestDto;
import com.smartparking.Smartparking.dto.response.reservation.ActiveReservationResponse;
import com.smartparking.Smartparking.dto.response.reservation.ReservationHistoryResponse;
import com.smartparking.Smartparking.dto.response.reservation.ReservationResponse;
import com.smartparking.Smartparking.entity.reservation.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationService {
    ReservationResponse createReservation(String userId, ReservationRequestDto request);

    List<ReservationHistoryResponse> getReservationHistory(String userId);

    Optional<ActiveReservationResponse> getActiveReservation(String userId);

    Reservation cancelReservation(String reservationId, String userId, CancelReservationRequest request);

    Reservation confirmReservation(String reservationId, String userId);

    Reservation activateReservation(String reservationId);

    void expirePendingReservations();
}