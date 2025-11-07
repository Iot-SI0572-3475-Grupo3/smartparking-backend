package com.smartparking.Smartparking.entity.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_queue")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "queue_id", length = 36, nullable = false)
    private String queueId;

    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20, nullable = false)
    private Priority priority;

    @Column(name = "scheduled_at")
    private LocalDateTime scheduledAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status = Status.pending;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    public enum Priority {
        low, medium, high, urgent
    }

    public enum Status {
        pending, processing, completed, failed
    }
}