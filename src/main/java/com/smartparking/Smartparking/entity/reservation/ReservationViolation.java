package com.smartparking.Smartparking.entity.reservation;

import com.smartparking.Smartparking.entity.iam.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_violations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationViolation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "violation_id", length = 36, nullable = false)
    private String violationId;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "violation_type", length = 30, nullable = false)
    private ViolationType violationType;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "penalty_amount", precision = 10, scale = 2)
    private BigDecimal penaltyAmount;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status = Status.pending;

    public enum ViolationType {
        no_show, late_arrival, late_departure, unauthorized_extension, invalid_vehicle
    }

    public enum Status {
        pending, resolver, appealed, dismissed
    }
}