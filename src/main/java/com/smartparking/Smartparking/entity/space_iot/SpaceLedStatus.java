package com.smartparking.Smartparking.entity.space_iot;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "space_led_status")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpaceLedStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "led_id", length = 36, nullable = false)
    private String ledId;

    @OneToOne
    @JoinColumn(name = "space_id", nullable = false)
    private ParkingSpace parkingSpace;

    @Enumerated(EnumType.STRING)
    @Column(name = "color", length = 10, nullable = false)
    private LedColor color = LedColor.off;

    @Column(name = "status")
    private Boolean status = false;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum LedColor {
        green, blue, red, yellow, off, blinking, error
    }
}