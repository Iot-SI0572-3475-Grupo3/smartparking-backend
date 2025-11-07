package com.smartparking.Smartparking.entity.iam;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id", length = 36)
    private String profileId;

    // ELIMINAR ESTE CAMPO → duplicado
    // private String userId;

    @Column(name = "first_name", length = 100)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relación 1:1 – DUEÑA DE LA FK
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    // Constructor sin userId duplicado
    public UserProfile(String firstName, String lastName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}