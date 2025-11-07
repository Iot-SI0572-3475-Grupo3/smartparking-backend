package com.smartparking.Smartparking.entity.space_iot;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "sensor_readings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SensorReading {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "reading_id", length = 36, nullable = false)
    private String readingId;

    @ManyToOne
    @JoinColumn(name = "sensor_id", nullable = false)
    private Sensor sensor;

    @Column(name = "distance_cm")
    private Double distanceCm;

    @Column(name = "vehicle_detected")
    private Boolean vehicleDetected = false;

    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}