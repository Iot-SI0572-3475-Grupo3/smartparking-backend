package com.smartparking.Smartparking.entity.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "template_id", length = 36, nullable = false)
    private String templateId;

    @Column(name = "name", length = 255, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 50, nullable = false)
    private Type type;

    @Column(name = "title_template", columnDefinition = "text", nullable = false)
    private String titleTemplate;

    @Column(name = "message_template", columnDefinition = "text", nullable = false)
    private String messageTemplate;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum Type {
        reservation_confirmed, reservation_cancelled, penalty_issued,
        space_available, system_alert, payment_confirmed, payment_failed
    }
}