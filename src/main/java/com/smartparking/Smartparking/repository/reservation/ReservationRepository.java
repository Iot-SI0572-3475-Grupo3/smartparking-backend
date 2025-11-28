package com.smartparking.Smartparking.repository.reservation;

import com.smartparking.Smartparking.entity.penalty.Absence;
import com.smartparking.Smartparking.entity.penalty.AbsenceCounter;
import com.smartparking.Smartparking.entity.penalty.PenaltyEvent;
import com.smartparking.Smartparking.entity.penalty.Suspension;
import com.smartparking.Smartparking.entity.reservation.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {

    @Query("""
        SELECT r FROM Reservation r 
        WHERE r.parkingSpace.spaceId = :spaceId 
          AND r.status IN ('pending', 'confirmed', 'active')
          AND (
            (r.endTime IS NULL AND r.startTime <= :endTime) OR
            (r.endTime IS NOT NULL AND r.startTime < :endTime AND r.endTime > :startTime)
          )
        """)
    List<Reservation> findOverlappingReservations(
            @Param("spaceId") String spaceId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    // ReservationRepository.java
    List<Reservation> findByUser_UserIdAndStatusInOrderByStartTimeDesc(
            String userId,
            Collection<Reservation.ReservationStatus> statuses
    );

    Optional<Reservation> findTopByUser_UserIdAndStatusInOrderByStartTimeDesc(
            String userId,
            Collection<Reservation.ReservationStatus> statuses
    );

    List<Reservation> findByStatusAndStartTimeBefore(
            Reservation.ReservationStatus status,
            LocalDateTime startTime
    );

    // ReservationRepository.java
    List<Reservation> findTop5ByUser_UserIdOrderByStartTimeDesc(String userId);

    // AbsenceRepository.java
    public interface AbsenceRepository extends JpaRepository<Absence, String> {}

    // AbsenceCounterRepository.java
    public interface AbsenceCounterRepository extends JpaRepository<AbsenceCounter, String> {
        Optional<AbsenceCounter> findByUserId(String userId);
    }

    // PenaltyEventRepository.java
    public interface PenaltyEventRepository extends JpaRepository<PenaltyEvent, String> {}

    // SuspensionRepository.java
    public interface SuspensionRepository extends JpaRepository<Suspension, String> {}

    // Busca por el CODE del ParkingSpace y ordena por fecha de creación DESC
    List<Reservation> findByParkingSpace_CodeOrderByCreatedAtDesc(String code);

    // Opcional: si también quieres paginación
    Page<Reservation> findByParkingSpace_CodeOrderByCreatedAtDesc(String code, Pageable pageable);

    // ReservationRepository.java → CAMBIA EL MÉTODO A ESTO:
    List<Reservation> findByParkingSpace_CodeOrderByStartTimeAsc(String code);

    // Opcional: si en el futuro necesitas paginación
    Page<Reservation> findByParkingSpace_CodeOrderByStartTimeDesc(String code, Pageable pageable);
}