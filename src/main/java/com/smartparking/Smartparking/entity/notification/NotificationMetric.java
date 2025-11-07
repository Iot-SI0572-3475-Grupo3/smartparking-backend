package com.smartparking.Smartparking.entity.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "metric_id", length = 36, nullable = false)
    private String metricId;

    @Column(name = "metric_name", length = 100, nullable = false)
    private String metricName;

    @Column(name = "metric_value", precision = 10, scale = 2)
    private BigDecimal metricValue;

    @Column(name = "metric_type", length = 100)
    private String metricType;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_period", length = 10, nullable = false)
    private TimePeriod timePeriod;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt = LocalDateTime.now();

    public enum TimePeriod {
        minute, hour, day, week, month
    }
}