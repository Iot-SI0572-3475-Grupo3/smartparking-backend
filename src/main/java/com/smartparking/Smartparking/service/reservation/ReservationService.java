package com.smartparking.Smartparking.service.reservation;

import com.smartparking.Smartparking.dto.request.reservation.CancelReservationRequest;
import com.smartparking.Smartparking.dto.request.reservation.ReservationRequestDto;
import com.smartparking.Smartparking.dto.response.reservation.ActiveReservationResponse;
import com.smartparking.Smartparking.dto.response.reservation.ReservationHistoryResponse;
import com.smartparking.Smartparking.dto.response.reservation.ReservationResponse;
import com.smartparking.Smartparking.entity.reservation.Reservation;
import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequestDto request);

    List<ReservationHistoryResponse> getReservationHistory(String userId);

    List<ReservationHistoryResponse> getAllReservationHistory();

    Optional<ActiveReservationResponse> getActiveReservation(String userId);

    Reservation cancelReservation(String reservationId, String userId, CancelReservationRequest request);

    Reservation confirmReservation(String reservationId, String userId);

    Reservation activateReservation(String reservationId);

    void expirePendingReservations();

    Reservation activateReservationBySpace(String spaceId);

    void expireReservationManually(String reservationId);

    List<ReservationResponse> getReservationsByParkingSpaceCode(String code);

    Page<ReservationResponse> getReservationsByParkingSpaceCode(String code, Pageable pageable);
}