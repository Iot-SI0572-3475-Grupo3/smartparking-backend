package com.smartparking.Smartparking.entity.iam;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_sessions")
@Data
@NoArgsConstructor
public class UserSession {

    @Id
    @Column(name = "session_id", length = 36)
    private String sessionId;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "token_hash", length = 255, nullable = false)
    private String tokenHash;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    public UserSession(String sessionId, String userId, String tokenHash, LocalDateTime createdAt, LocalDateTime expiresAt, Boolean isActive) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.tokenHash = tokenHash;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.isActive = isActive;
    }
}