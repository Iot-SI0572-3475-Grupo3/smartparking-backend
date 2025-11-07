package com.smartparking.Smartparking.entity.reservation;

import com.fasterxml.jackson.databind.JsonNode;
import com.smartparking.Smartparking.converter.JsonNodeConverter;
import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.entity.penalty.Absence;
import com.smartparking.Smartparking.entity.space_iot.ArrivalEvent;
import com.smartparking.Smartparking.entity.space_iot.DepartureEvent;
import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reservation_id", length = 36, nullable = false)
    private String reservationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Dentro de la clase Reservation, agrega:

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepartureEvent> departureEvents = new ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArrivalEvent> arrivalEvents = new ArrayList<>();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Absence> absences = new ArrayList<>();


    @ManyToOne
    @JoinColumn(name = "space_id", nullable = false)
    private ParkingSpace parkingSpace;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "date")
    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private ReservationStatus status = ReservationStatus.pending;

    @Column(name = "vehicle_info", columnDefinition = "text")
    private String vehicleInfo;

    @Column(name = "special_requirements", length = 255)
    private String specialRequirements;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancellation_reason", columnDefinition = "text")
    private String cancellationReason;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", length = 20)
    private PaymentStatus paymentStatus;

    @Column(name = "payment_id", length = 36)
    private String paymentId;

    // Enums
    public enum ReservationStatus {
        pending, confirmed, active, completed, cancelled, expired
    }

    public enum PaymentStatus {
        pending, paid, failed, refunded
    }
}