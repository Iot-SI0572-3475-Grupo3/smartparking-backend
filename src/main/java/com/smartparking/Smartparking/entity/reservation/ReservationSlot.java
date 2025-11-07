package com.smartparking.Smartparking.entity.reservation;

import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_slots")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservationSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "slot_id", length = 36, nullable = false)
    private String slotId;

    @ManyToOne
    @JoinColumn(name = "space_id", nullable = false)
    private ParkingSpace parkingSpace;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @Column(name = "price_per_hour", precision = 8, scale = 2)
    private BigDecimal pricePerHour;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}