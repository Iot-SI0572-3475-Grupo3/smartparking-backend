package com.smartparking.Smartparking.entity.analytics;

import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "time_tracking")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeTracking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "tracking_id", length = 36, nullable = false)
    private String trackingId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "space_id", nullable = false)
    private ParkingSpace parkingSpace;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private Integer duration;  // en minutos

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status = Status.active;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "timeTracking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsageMetric> usageMetrics = new ArrayList<>();

    @OneToMany(mappedBy = "timeTracking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserBehaviorPattern> behaviorPatterns = new ArrayList<>();

    public enum Status {
        active, completed, cancelled
    }
}