package com.smartparking.Smartparking.entity.reservation;

import com.smartparking.Smartparking.entity.iam.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "history_id", length = 36, nullable = false)
    private String historyId;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 20, nullable = false)
    private EventType eventType;

    @Column(name = "event_data", columnDefinition = "json")
    private String eventData;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum EventType {
        created, confirmed, activated, completed, cancelled, expired, extended
    }
}