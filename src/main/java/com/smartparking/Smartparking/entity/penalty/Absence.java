package com.smartparking.Smartparking.entity.penalty;

import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.entity.reservation.Reservation;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "absences")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Absence {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "absence_id", length = 36, nullable = false)
    private String absenceId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "detected_at")
    private LocalDateTime detectedAt = LocalDateTime.now();
}