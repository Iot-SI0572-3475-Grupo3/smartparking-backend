package com.smartparking.Smartparking.entity.penalty;

import com.smartparking.Smartparking.entity.iam.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "penalty_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PenaltyEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id", length = 36, nullable = false)
    private String eventId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "event_type", length = 100, nullable = false)
    private String eventType;

    @Column(name = "payload", columnDefinition = "json")
    private String payload;

    @Column(name = "occured")
    private LocalDateTime occured = LocalDateTime.now();
}