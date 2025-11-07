package com.smartparking.Smartparking.entity.notification;

import com.smartparking.Smartparking.entity.iam.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_device_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeviceToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "token_id", length = 36, nullable = false)
    private String tokenId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "token", length = 255, nullable = false)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform", length = 20, nullable = false)
    private Platform platform;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    public enum Platform {
        android, ios, web
    }
}