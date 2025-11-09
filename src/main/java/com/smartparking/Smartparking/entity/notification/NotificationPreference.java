package com.smartparking.Smartparking.entity.notification;

import com.smartparking.Smartparking.entity.iam.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "notification_preferences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "preference_id", length = 36, nullable = false)
    private String preferenceId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", length = 50, nullable = false)
    private NotificationType notificationType;

    @Column(name = "channel", length = 20, nullable = false)
    private String channel;

    @Column(name = "is_enabled")
    private Boolean isEnabled = true;

    @Column(name = "quiet_hours_start")
    private LocalTime quietHoursStart;

    @Column(name = "quiet_hours_end")
    private LocalTime quietHoursEnd;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum NotificationType {
        reservation_confirmed, reservation_cancelled, penalty_issued,
        space_available, system_alert, payment_confirmed, payment_failed
    }

    public enum Channel { email, sms, push, in_app }
}