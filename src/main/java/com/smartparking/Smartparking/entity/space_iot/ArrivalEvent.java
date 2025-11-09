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

    @Column(name = "reservation_id", length = 36, nullable = false)
    private String reservationId;

    @Column(name = "space_id", length = 36, nullable = false)
    private String spaceId;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}