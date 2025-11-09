package com.smartparking.Smartparking.entity.penalty;

import com.smartparking.Smartparking.entity.iam.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "suspensions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suspension {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "suspension_id", length = 36, nullable = false)
    private String suspensionId;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status = Status.active;

    public enum Status {
        active, completed
    }
}