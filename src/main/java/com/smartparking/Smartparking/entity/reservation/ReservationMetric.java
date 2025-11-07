package com.smartparking.Smartparking.entity.reservation;

import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_metrics")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationMetric {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "metric_id", length = 36, nullable = false)
    private String metricId;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "space_id", nullable = false)
    private ParkingSpace parkingSpace;

    @Column(name = "metric_name", length = 100, nullable = false)
    private String metricName;

    @Column(name = "metric_value", precision = 10, scale = 2)
    private BigDecimal metricValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_period", length = 10, nullable = false)
    private TimePeriod timePeriod;

    @Column(name = "recorded_at")
    private LocalDateTime recordedAt = LocalDateTime.now();

    @Column(name = "additional_data", columnDefinition = "json")
    private String additionalData;

    public enum TimePeriod {
        day, week, month
    }
}