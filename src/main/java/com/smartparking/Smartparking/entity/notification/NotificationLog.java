package com.smartparking.Smartparking.entity.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "log_id", length = 36, nullable = false)
    private String logId;

    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", length = 20, nullable = false)
    private EventType eventType;

    @Column(name = "event_data", columnDefinition = "json")
    private String eventData;

    @Column(name = "occurred_at")
    private LocalDateTime occurredAt = LocalDateTime.now();

    @Column(name = "user_id", length = 36)
    private String userId;

    public enum EventType {
        created, sent, delivered, failed, expired, retry, cancelled
    }
}