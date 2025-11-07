package com.smartparking.Smartparking.entity.time;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "timers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "timer_id", length = 36, nullable = false)
    private String timerId;

    @ManyToOne
    @JoinColumn(name = "time_session_id", nullable = false)
    private TimeSession timeSession;

    @Column(name = "start_tick", nullable = false)
    private LocalDateTime startTick;

    @Column(name = "stop_tick")
    private LocalDateTime stopTick;

    @Column(name = "elapsed_min")
    private Integer elapsedMin;
}