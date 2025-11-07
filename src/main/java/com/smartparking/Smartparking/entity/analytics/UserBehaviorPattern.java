package com.smartparking.Smartparking.entity.analytics;

import com.smartparking.Smartparking.entity.iam.User;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_behavior_patterns")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserBehaviorPattern {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "pattern_id", length = 36, nullable = false)
    private String patternId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "tracking_id", nullable = false)
    private TimeTracking timeTracking;

    @Column(name = "frequent_spaces", columnDefinition = "json")
    private String frequentSpaces;  // JSON: ["A01", "B05"]

    @Column(name = "peak_hours")
    private Integer peakHours;  // hora más frecuente (0-23)

    @Column(name = "average_duration")
    private Integer averageDuration;  // en minutos

    @Column(name = "total_uses")
    private Integer totalUses;

    @Column(name = "last_analysis_at")
    private LocalDateTime lastAnalysisAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}