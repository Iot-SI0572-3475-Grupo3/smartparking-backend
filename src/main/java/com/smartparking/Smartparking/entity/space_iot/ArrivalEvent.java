package com.smartparking.Smartparking.entity.space_iot;

import com.smartparking.Smartparking.entity.reservation.Reservation;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "arrival_events")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArrivalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "event_id", length = 36, nullable = false)
    private String eventId;

    @ManyToOne
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @ManyToOne
    @JoinColumn(name = "space_id", nullable = false)
    private ParkingSpace parkingSpace;

    @Column(name = "timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}