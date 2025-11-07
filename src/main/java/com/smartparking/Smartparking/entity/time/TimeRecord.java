package com.smartparking.Smartparking.entity.time;

import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.entity.space_iot.ParkingSpace;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "time_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimeRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "time_record_id", length = 36, nullable = false)
    private String timeRecordId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "parking_space_id", nullable = false)
    private ParkingSpace parkingSpace;

    @Column(name = "duration_min")
    private Integer durationMin;

    @Column(name = "date_recorded")
    private LocalDateTime dateRecorded = LocalDateTime.now();
}