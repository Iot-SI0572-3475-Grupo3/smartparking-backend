package com.smartparking.Smartparking.entity.penalty;

import com.smartparking.Smartparking.entity.iam.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "warnings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Warning {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "warning_id", length = 36, nullable = false)
    private String warningId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "message", length = 255)
    private String message;

    @Column(name = "created")
    private LocalDateTime created = LocalDateTime.now();
}