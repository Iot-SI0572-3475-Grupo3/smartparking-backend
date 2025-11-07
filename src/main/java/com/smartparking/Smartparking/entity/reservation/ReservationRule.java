package com.smartparking.Smartparking.entity.reservation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "rule_id", length = 36, nullable = false)
    private String ruleId;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private RuleType type;

    @Column(name = "value")
    private String value;

    @Enumerated(EnumType.STRING)
    @Column(name = "applies_to", length = 20, nullable = false)
    private AppliesTo appliesTo;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum RuleType {
        max_duration, min_duration, advance_booking, cancellation_time,
        max_reservations_per_day, max_reservation_per_week
    }

    public enum AppliesTo {
        all_users, students, faculty, staff, administrators
    }
}