package com.smartparking.Smartparking.entity.penalty;

import com.smartparking.Smartparking.entity.iam.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "absence_counters")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "counter_id", length = 36, nullable = false)
    private String counterId;

    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "absence_count")
    private Integer absenceCount = 0;

    @Column(name = "strike_count")
    private Integer strikeCount = 0;

    @Column(name = "max_strikes")
    private Integer maxStrikes = 3;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();
}