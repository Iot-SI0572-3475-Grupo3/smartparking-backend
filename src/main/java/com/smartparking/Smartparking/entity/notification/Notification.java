package com.smartparking.Smartparking.entity.notification;

import com.smartparking.Smartparking.entity.iam.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "notification_id", length = 36, nullable = false)
    private String notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "text", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private Type type;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20, nullable = false)
    private Priority priority = Priority.medium;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status = Status.pending;

    @Enumerated(EnumType.STRING)
    @Column(name = "channel", length = 20, nullable = false)
    private Channel channel;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "metadata", columnDefinition = "json")
    private String metadata;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "error_message", columnDefinition = "text")
    private String errorMessage;

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationQueue> queueEntries = new ArrayList<>();

    @OneToMany(mappedBy = "notification", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationLog> logs = new ArrayList<>();

    public enum Type {
        reservation_confirmed, reservation_cancelled, penalty_issued,
        space_available, system_alert, payment_confirmed, payment_failed
    }

    public enum Priority {
        low, medium, high, urgent
    }

    public enum Status {
        pending, sent, delivered, failed, expired
    }

    public enum Channel {
        email, sms, push, in_app
    }
}