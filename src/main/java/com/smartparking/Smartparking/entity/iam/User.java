package com.smartparking.Smartparking.entity.iam;

import com.smartparking.Smartparking.entity.notification.Notification;
import com.smartparking.Smartparking.entity.notification.NotificationPreference;
import com.smartparking.Smartparking.entity.notification.UserDeviceToken;
import com.smartparking.Smartparking.entity.penalty.*;
import com.smartparking.Smartparking.entity.reservation.Reservation;
import com.smartparking.Smartparking.entity.reservation.ReservationHistory;
import com.smartparking.Smartparking.entity.reservation.ReservationViolation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "user_id", length = 36)
    private String userId;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "failed_login_attempt")
    private Integer failedLoginAttempt;

    // Relación 1:1 inversa
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserProfile profile;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserSession> userSessions;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Warning> warnings = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationHistory> reservationHistory = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationViolation> reservationViolations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserDeviceToken> deviceTokens = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NotificationPreference> preferences = new ArrayList<>();

    // Enums
    public enum Role { university_member, administrator }
    public enum Status { active, inactive, suspended }

    // Constructor útil
    public User(String userId, String email, String passwordHash, Role role, Status status, LocalDateTime createdAt) {
        this.userId = userId;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = status;
        this.createdAt = createdAt;
    }
}