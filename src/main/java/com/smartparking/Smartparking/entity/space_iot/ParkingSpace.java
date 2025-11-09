package com.smartparking.Smartparking.entity.space_iot;

import com.smartparking.Smartparking.entity.reservation.ReservationMetric;
import com.smartparking.Smartparking.entity.reservation.ReservationSlot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_spaces")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParkingSpace {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "space_id", length = 36, nullable = false)
    private String spaceId;

    @Column(name = "code", length = 10, nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private SpaceStatus status = SpaceStatus.available;

    @Column(name = "current_reservation_id", length = 36)
    private String currentReservationId;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToOne(mappedBy = "parkingSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private SpaceLedStatus ledStatus;

    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationSlot> slots = new ArrayList<>();

    @OneToMany(mappedBy = "parkingSpace", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReservationMetric> metrics = new ArrayList<>();

    public enum SpaceStatus {
        available, reserved, occupied, maintenance
    }
}