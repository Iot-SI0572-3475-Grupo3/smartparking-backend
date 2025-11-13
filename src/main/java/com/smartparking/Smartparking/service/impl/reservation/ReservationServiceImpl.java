package com.smartparking.Smartparking.service.impl.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smartparking.Smartparking.dto.request.reservation.CancelReservationRequest;
import com.smartparking.Smartparking.dto.request.reservation.ReservationRequestDto;
import com.smartparking.Smartparking.dto.response.reservation.ActiveReservationResponse;
import com.smartparking.Smartparking.dto.response.reservation.ReservationHistoryResponse;
import com.smartparking.Smartparking.dto.response.reservation.ReservationResponse;
import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.entity.notification.NotificationPreference;
import com.smartparking.Smartparking.entity.penalty.Absence;
import com.smartparking.Smartparking.entity.penalty.AbsenceCounter;
import com.smartparking.Smartparking.entity.penalty.PenaltyEvent;
import com.smartparking.Smartparking.entity.penalty.Suspension;
import com.smartparking.Smartparking.entity.reservation.Reservation;
import com.smartparking.Smartparking.entity.space_iot.ArrivalEvent;
import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import com.smartparking.Smartparking.exception.ResourceNotFoundException;
import com.smartparking.Smartparking.exception.BadRequestException;
import com.smartparking.Smartparking.repository.UserRepository;
import com.smartparking.Smartparking.repository.penalty.AbsenceCounterRepository;
import com.smartparking.Smartparking.repository.penalty.AbsenceRepository;
import com.smartparking.Smartparking.repository.penalty.PenaltyEventRepository;
import com.smartparking.Smartparking.repository.penalty.SuspensionRepository;
import com.smartparking.Smartparking.repository.reservation.ReservationRepository;
import com.smartparking.Smartparking.repository.space_iot.ArrivalEventRepository;
import com.smartparking.Smartparking.repository.space_iot.ParkingSpaceRepository;
import com.smartparking.Smartparking.service.notification.NotificationService;
import com.smartparking.Smartparking.service.reservation.ReservationService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ParkingSpaceRepository parkingSpaceRepository;
    private final UserRepository userRepository;
    private final ArrivalEventRepository arrivalEventRepository;
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private final AbsenceRepository absenceRepository;
    private final AbsenceCounterRepository absenceCounterRepository;
    private final PenaltyEventRepository penaltyEventRepository;
    private final SuspensionRepository suspensionRepository;

    private static final BigDecimal COST_PER_HOUR = new BigDecimal("2.50");

    @Override
    @Transactional
    public ReservationResponse createReservation(String userId, ReservationRequestDto request) {

        // 1. Validar usuario
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // 2. Validar espacio
        ParkingSpace space = parkingSpaceRepository.findById(request.getSpaceId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espacio no encontrado"));

        if (space.getStatus() != ParkingSpace.SpaceStatus.available) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El espacio no está disponible");
        }

        // 3. Validar que no haya reserva activa en este espacio
        boolean hasActiveReservation = reservationRepository.findOverlappingReservations(
                        request.getSpaceId(),
                        request.getStartTime(),
                        request.getStartTime().plusMinutes(1) // pequeño rango para detectar colisión
                ).stream()
                .anyMatch(r -> r.getStatus() == Reservation.ReservationStatus.pending ||
                        r.getStatus() == Reservation.ReservationStatus.confirmed ||
                        r.getStatus() == Reservation.ReservationStatus.active);

        if (hasActiveReservation) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El espacio ya tiene una reserva activa");
        }

        // 4. Crear reserva (endTime = null)
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setParkingSpace(space);
        reservation.setStartTime(request.getStartTime());
        reservation.setEndTime(null);
        reservation.setDate(request.getStartTime());
        reservation.setStatus(Reservation.ReservationStatus.pending);
        reservation.setVehicleInfo(request.getVehicleInfo());           // JSON → vehicle_info
        reservation.setSpecialRequirements(request.getSpecialRequirements()); // Texto → special_requirements

        reservation.setTotalCost(BigDecimal.ZERO);
        reservation.setPaymentStatus(Reservation.PaymentStatus.pending);

        reservation = reservationRepository.save(reservation);

        // 5. Actualizar estado del espacio
        space.setStatus(ParkingSpace.SpaceStatus.reserved);
        space.setCurrentReservationId(reservation.getReservationId());
        parkingSpaceRepository.save(space);

        // 6. Respuesta
        return ReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .spaceCode(space.getCode())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .date(reservation.getDate())
                .status(reservation.getStatus().name().toLowerCase())
                .vehicleInfo(reservation.getVehicleInfo())                    // String JSON
                .specialRequirements(reservation.getSpecialRequirements())   // Texto plano
                .totalCost(reservation.getTotalCost())
                .paymentStatus(reservation.getPaymentStatus().name().toLowerCase())
                .createdAt(reservation.getCreatedAt())
                .build();
    }

    @Override
    public List<ReservationHistoryResponse> getReservationHistory(String userId) {

        List<Reservation.ReservationStatus> finalStatuses = List.of(
                Reservation.ReservationStatus.completed,
                Reservation.ReservationStatus.cancelled,
                Reservation.ReservationStatus.expired
        );

        return reservationRepository
                .findByUser_UserIdAndStatusInOrderByStartTimeDesc(userId, finalStatuses)
                .stream()
                .map(res -> ReservationHistoryResponse.builder()
                        .reservationId(res.getReservationId())
                        .spaceCode(res.getParkingSpace().getCode())
                        .startTime(res.getStartTime())
                        .endTime(res.getEndTime())
                        .date(res.getDate())
                        .status(res.getStatus().name().toLowerCase())
                        .vehicleInfo(res.getVehicleInfo())
                        .specialRequirements(res.getSpecialRequirements())
                        .totalCost(res.getTotalCost() != null ? res.getTotalCost() : BigDecimal.ZERO)
                        .completedAt(res.getCompletedAt())
                        .cancelledAt(res.getCancelledAt())
                        .cancellationReason(res.getCancellationReason())
                        .build())
                .toList();
    }

    @Override
    public Optional<ActiveReservationResponse> getActiveReservation(String userId) {

        List<Reservation.ReservationStatus> activeStatuses = List.of(
                Reservation.ReservationStatus.pending,
                Reservation.ReservationStatus.confirmed,
                Reservation.ReservationStatus.active
        );

        return reservationRepository
                .findTopByUser_UserIdAndStatusInOrderByStartTimeDesc(userId, activeStatuses)
                .map(res -> {
                    LocalDateTime now = LocalDateTime.now();
                    long minutesUntilArrival = java.time.Duration.between(now, res.getStartTime()).toMinutes();

                    // Lógica: puede cancelar si faltan más de 15 minutos
                    boolean canCancel = minutesUntilArrival > 15;

                    return ActiveReservationResponse.builder()
                            .reservationId(res.getReservationId())
                            .spaceCode(res.getParkingSpace().getCode())
                            .startTime(res.getStartTime())
                            .endTime(res.getEndTime())
                            .status(res.getStatus().name().toLowerCase())
                            .vehicleInfo(res.getVehicleInfo())
                            .specialRequirements(res.getSpecialRequirements())
                            .minutesUntilArrival(minutesUntilArrival)
                            .canCancel(canCancel)
                            .build();
                });
    }

    @Override
    @Transactional
    public Reservation cancelReservation(String reservationId, String userId, CancelReservationRequest request) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        // Validar que pertenece al usuario
        if (!reservation.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes cancelar esta reserva");
        }

        // Validar estado: solo pending, confirmed o active
        if (!Set.of(Reservation.ReservationStatus.pending, Reservation.ReservationStatus.confirmed, Reservation.ReservationStatus.active)
                .contains(reservation.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Esta reserva no se puede cancelar");
        }

        // Validar tiempo: no se puede cancelar si faltan menos de 15 minutos
        long minutesUntilStart = Duration.between(LocalDateTime.now(), reservation.getStartTime()).toMinutes();
        if (minutesUntilStart < 15) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No puedes cancelar con menos de 15 minutos de anticipación");
        }

        // Actualizar estado
        reservation.setStatus(Reservation.ReservationStatus.cancelled);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setCancellationReason(request.getReason());

        // Liberar espacio
        ParkingSpace space = reservation.getParkingSpace();
        space.setStatus(ParkingSpace.SpaceStatus.available);
        space.setCurrentReservationId(null);
        parkingSpaceRepository.save(space);

        reservation = reservationRepository.save(reservation);

        Map<String, Object> data = Map.of(
                "spaceCode", space.getCode(),
                "reason", request.getReason() != null ? request.getReason() : "No especificado"
        );

        notificationService.sendIfEnabled(
                userId,
                NotificationPreference.NotificationType.reservation_cancelled,
                data
        );

        return reservation;
    }

    @Override
    @Transactional
    public Reservation confirmReservation(String reservationId, String userId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        // Validar usuario
        if (!reservation.getUser().getUserId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes confirmar esta reserva");
        }

        // Solo se puede confirmar si está en pending
        if (reservation.getStatus() != Reservation.ReservationStatus.pending) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden confirmar reservas pendientes");
        }

        // Validar que no haya expirado
        if (reservation.getStartTime().isBefore(LocalDateTime.now().minusMinutes(5))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La reserva ha expirado o ya comenzó");
        }

        reservation.setStatus(Reservation.ReservationStatus.confirmed);
        reservation.setConfirmedAt(LocalDateTime.now());

        reservation = reservationRepository.save(reservation);

        // NOTIFICACIÓN: Reserva confirmada
        Map<String, Object> data = Map.of(
                "spaceCode", reservation.getParkingSpace().getCode(),
                "startTime", reservation.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                "date", reservation.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        );

        notificationService.sendIfEnabled(
                userId,
                NotificationPreference.NotificationType.reservation_confirmed,
                data
        );

        return reservation;
    }

    @Override
    @Transactional
    public Reservation activateReservation(String reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva no encontrada"));

        // Solo se puede activar si está pending o confirmed
        if (!Set.of(Reservation.ReservationStatus.pending, Reservation.ReservationStatus.confirmed).contains(reservation.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La reserva no se puede activar");
        }

        // Validar que esté dentro del rango de llegada (±15 min)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = reservation.getStartTime();
        if (now.isBefore(start.minusMinutes(15)) || now.isAfter(start.plusMinutes(30))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fuera del horario de llegada permitido");
        }

        reservation.setStatus(Reservation.ReservationStatus.active);
        // Opcional: registrar arrival event
        // arrivalEventService.create(reservation, now);

        return reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void expirePendingReservations() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(15);

        List<Reservation> expired = reservationRepository.findByStatusAndStartTimeBefore(
                Reservation.ReservationStatus.pending, threshold
        );

        for (Reservation res : expired) {
            String userId = res.getUser().getUserId();
            ParkingSpace space = res.getParkingSpace();

            // 1. Expirar reserva
            res.setStatus(Reservation.ReservationStatus.expired);
            res.setCompletedAt(LocalDateTime.now());

            // 2. Liberar espacio
            space.setStatus(ParkingSpace.SpaceStatus.available);
            space.setCurrentReservationId(null);
            parkingSpaceRepository.save(space);

            // 3. Registrar ausencia
            Absence absence = new Absence();
            absence.setUserId(userId);
            absence.setReservationId(res.getReservationId());
            absence.setDetectedAt(LocalDateTime.now());
            absenceRepository.save(absence);

            // 4. Actualizar contador
            AbsenceCounter counter = absenceCounterRepository.findByUserId(userId)
                    .orElseGet(() -> {
                        AbsenceCounter c = new AbsenceCounter();
                        c.setUserId(userId);
                        c.setAbsenceCount(0);
                        c.setStrikeCount(0);
                        c.setMaxStrikes(3);
                        return c;
                    });

            counter.setAbsenceCount(counter.getAbsenceCount() + 1);
            counter.setStrikeCount(counter.getStrikeCount() + 1);
            counter.setLastUpdated(LocalDateTime.now());
            absenceCounterRepository.save(counter);

            // 5. Penalización si max strikes
            if (counter.getStrikeCount() >= counter.getMaxStrikes()) {
                PenaltyEvent penalty = new PenaltyEvent();
                penalty.setUserId(userId);
                penalty.setEventType("ABSENCE_PENALTY");
                penalty.setPayload("{\"absenceId\": \"" + absence.getAbsenceId() + "\", \"reason\": \"expired_reservation\"}");
                penalty.setOccured(LocalDateTime.now());
                penaltyEventRepository.save(penalty);

                Suspension suspension = new Suspension();
                suspension.setUserId(userId);
                suspension.setStartDate(LocalDateTime.now());
                suspension.setEndDate(LocalDateTime.now().plusDays(3)); // 3 días de suspensión
                suspension.setStatus(Suspension.Status.active);
                suspensionRepository.save(suspension);
            }

            // 6. Notificación
            Map<String, Object> data = Map.of(
                    "spaceCode", space.getCode(),
                    "strikeCount", counter.getStrikeCount(),
                    "maxStrikes", counter.getMaxStrikes(),
                    "reason", "Reserva expirada (no confirmada a tiempo)"
            );

            notificationService.sendIfEnabled(
                    userId,
                    NotificationPreference.NotificationType.penalty_issued,
                    data
            );
        }

        if (!expired.isEmpty()) {
            reservationRepository.saveAll(expired);
        }
    }

    @Override
    @Transactional
    public Reservation activateReservationBySpace(String spaceId) {

        ParkingSpace space = parkingSpaceRepository.findById(spaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Espacio no encontrado"));

        String currentResId = space.getCurrentReservationId();
        if (currentResId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No hay reserva activa para este espacio");
        }

        Reservation reservation = reservationRepository.findById(currentResId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Reserva activa no encontrada"));

        if (reservation.getStatus() != Reservation.ReservationStatus.confirmed) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La reserva no está confirmada para activar");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = reservation.getStartTime();
        if (now.isBefore(start.minusMinutes(15)) || now.isAfter(start.plusMinutes(15))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fuera del ventana de llegada para activación automática");
        }

        reservation.setStatus(Reservation.ReservationStatus.active);

        ArrivalEvent arrival = new ArrivalEvent();
        arrival.setReservationId(reservation.getReservationId());
        arrival.setSpaceId(spaceId);
        arrival.setTimestamp(now);
        arrivalEventRepository.save(arrival);

        reservation = reservationRepository.save(reservation);

        Map<String, Object> data = Map.of(
                "spaceCode", reservation.getParkingSpace().getCode(),
                "message", "¡Tu sesión ha comenzado!"
        );

        notificationService.sendIfEnabled(
                reservation.getUser().getUserId(),
                NotificationPreference.NotificationType.system_alert,
                data
        );

        return reservation;
    }

    @Override
    @Transactional
    public void expireReservationManually(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada: " + reservationId));

        // Validar estado
        if (reservation.getStatus() != Reservation.ReservationStatus.confirmed &&
                reservation.getStatus() != Reservation.ReservationStatus.pending) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Solo reservas pending o confirmed pueden expirar."
            );
        }

        LocalDateTime now = LocalDateTime.now();
        String userId = reservation.getUser().getUserId(); // ← CORREGIDO
        ParkingSpace space = reservation.getParkingSpace();

        // 1. Registrar ausencia
        Absence absence = new Absence();
        absence.setUserId(userId);
        absence.setReservationId(reservationId);
        absence.setDetectedAt(now);
        absenceRepository.save(absence);

        // 2. Actualizar contador
        AbsenceCounter counter = absenceCounterRepository.findByUserId(userId)
                .orElseGet(() -> {
                    AbsenceCounter c = new AbsenceCounter();
                    c.setUserId(userId);
                    c.setAbsenceCount(0);
                    c.setStrikeCount(0);
                    c.setMaxStrikes(3);
                    return c;
                });

        counter.setAbsenceCount(counter.getAbsenceCount() + 1);
        counter.setStrikeCount(counter.getStrikeCount() + 1);
        counter.setLastUpdated(now);
        absenceCounterRepository.save(counter);

        // 3. Penalización
        if (counter.getStrikeCount() >= counter.getMaxStrikes()) {
            PenaltyEvent penalty = new PenaltyEvent();
            penalty.setUserId(userId);
            penalty.setEventType("ABSENCE_PENALTY");
            penalty.setPayload("{\"absenceId\": \"" + absence.getAbsenceId() + "\", \"reason\": \"no_show\"}");
            penalty.setOccured(now);
            penaltyEventRepository.save(penalty);

            Suspension suspension = new Suspension();
            suspension.setUserId(userId);
            suspension.setStartDate(now);
            suspension.setEndDate(now.plusDays(3));
            suspension.setStatus(Suspension.Status.active);
            suspensionRepository.save(suspension);
        }

        // 4. Expirar reserva
        reservation.setStatus(Reservation.ReservationStatus.expired);
        reservation.setCompletedAt(now);
        reservationRepository.save(reservation);

        // 5. Liberar espacio
        space.setStatus(ParkingSpace.SpaceStatus.available);
        space.setCurrentReservationId(null);
        parkingSpaceRepository.save(space);

        // 6. Notificación
        Map<String, Object> data = Map.of(
                "spaceCode", space.getCode(),
                "strikeCount", counter.getStrikeCount(),
                "maxStrikes", counter.getMaxStrikes(),
                "reason", "No llegaste a tiempo al espacio"
        );

        notificationService.sendIfEnabled(
                userId,
                NotificationPreference.NotificationType.penalty_issued,
                data
        );
    }
}