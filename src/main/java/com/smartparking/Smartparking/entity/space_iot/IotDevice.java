package com.smartparking.Smartparking.entity.space_iot;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "iot_devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IotDevice {
    @Id
    @GeneratedValue
    @Column(name = "device_id", length = 36)
    private String deviceId;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "ip_address", length = 15, nullable = false)
    private String ipAddress;

    @Column(name = "mac_address", length = 17, nullable = false)
    private String macAddress;

    @Column(name = "is_connected")
    private Boolean isConnected = false;

    @Column(name = "last_sync")
    private LocalDateTime lastSync;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}