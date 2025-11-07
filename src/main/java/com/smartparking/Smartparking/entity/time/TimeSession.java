package com.smartparking.Smartparking.entity.time;

import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "time_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeSession {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "time_session_id", length = 36, nullable = false)
    private String timeSessionId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "parking_space_id", nullable = false)
    private ParkingSpace parkingSpace;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @Column(name = "duration_minutes")
    private Integer durationMinutes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private Status status = Status.active;

    @OneToMany(mappedBy = "timeSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Timer> timers = new ArrayList<>();

    public enum Status {
        active, completed, cancelled
    }
}