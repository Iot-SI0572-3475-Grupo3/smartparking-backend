package com.smartparking.Smartparking.entity.space_iot;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sensors")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "sensor_id", length = 36, nullable = false)
    private String sensorId;

    // FK: space_id → parking_spaces.space_id
    @OneToOne
    @JoinColumn(name = "space_id", nullable = false)  // FK en sensors
    private ParkingSpace parkingSpace;

    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private IotDevice iotDevice;

    @Column(name = "last_distance")
    private Double lastDistance;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20, nullable = false)
    private SensorState state = SensorState.inactive;

    @Column(name = "last_detected")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "created_at")
    private LocalDateTime lastDetected = LocalDateTime.now();

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SensorReading> readings = new ArrayList<>();

    public enum SensorState {
        active, inactive, error
    }
}