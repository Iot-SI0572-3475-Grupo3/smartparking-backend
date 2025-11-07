package com.smartparking.Smartparking.entity.analytics;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "efficiency_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EfficiencyReport {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "report_id", length = 36, nullable = false)
    private String reportId;

    @Column(name = "space_id", length = 36)
    private String spaceId;

    @Column(name = "period_start", nullable = false)
    private LocalDateTime periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDateTime periodEnd;

    @Column(name = "total_time_occupied")
    private Integer totalTimeOccupied;

    @Column(name = "total_time_available")
    private Integer totalTimeAvailable;

    @Column(name = "efficiency_rate", precision = 5, scale = 2)
    private BigDecimal efficiencyRate;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}