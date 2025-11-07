package com.smartparking.Smartparking.service.impl.analytics;


import com.smartparking.Smartparking.dto.response.analytics.DashboardResponse;
import com.smartparking.Smartparking.dto.response.iam.SessionResponse;
import com.smartparking.Smartparking.dto.response.space_iot.ParkingSpaceResponse;
import com.smartparking.Smartparking.entity.reservation.Reservation;
import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import com.smartparking.Smartparking.repository.penalty.AbsenceCounterRepository;
import com.smartparking.Smartparking.repository.reservation.ReservationRepository;
import com.smartparking.Smartparking.repository.space_iot.ParkingSpaceRepository;
import com.smartparking.Smartparking.service.analytics.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final ParkingSpaceRepository parkingSpaceRepository;
    private final ReservationRepository reservationRepository;
    private final AbsenceCounterRepository absenceCounterRepository;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public DashboardResponse getUserDashboard(String userId) {
        List<ParkingSpace> availableSpaces = parkingSpaceRepository.findAvailableSpaces();
        List<Reservation> recentReservations = null;
        Long absenceCount = absenceCounterRepository.getTotalAbsenceCountByUserId(userId);

        return DashboardResponse.builder()
                .availableSpaces(mapToSpaceResponse(availableSpaces))
                .recentSessions(mapToSessionResponse(recentReservations))
                .absenceCount(absenceCount != null ? absenceCount.intValue() : 0)
                .canReserve(true)
                .build();
    }

    private List<ParkingSpaceResponse> mapToSpaceResponse(List<ParkingSpace> spaces) {
        return spaces.stream()
                .map(ps -> ParkingSpaceResponse.builder()
                        .spaceId(ps.getSpaceId())
                        .code(ps.getCode())
                        .status(ps.getStatus().name().toLowerCase()) // "available"
                        .build())
                .toList();
    }

    private List<SessionResponse> mapToSessionResponse(List<Reservation> reservations) {
        return reservations.stream()
                .map(r -> SessionResponse.builder()
                        .spaceCode(r.getParkingSpace().getCode())
                        .date(r.getDate() != null
                                ? r.getDate().format(DATE_FMT)
                                : r.getStartTime().toLocalDate().format(DATE_FMT))
                        .start(r.getStartTime().toLocalTime().format(TIME_FMT))
                        .end(r.getEndTime().toLocalTime().format(TIME_FMT))
                        .build())
                .toList();
    }
}