package com.smartparking.Smartparking.entity.analytics;

import com.smartparking.Smartparking.entity.reservation.Reservation;
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
@Table(name = "usage_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsageMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "metric_id", length = 36, nullable = false)
    private String metricId;

    @ManyToOne
    @JoinColumn(name = "space_id", nullable = false)
    private ParkingSpace parkingSpace;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "tracking_id", nullable = false)
    private TimeTracking timeTracking;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}