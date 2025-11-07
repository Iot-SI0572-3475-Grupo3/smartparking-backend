package com.smartparking.Smartparking.entity.analytics;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Entity
@Table(name = "dashboards")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Dashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "dashboard_id", length = 36, nullable = false)
    private String dashboardId;

    @Column(name = "widgets", columnDefinition = "json")
    private String widgets;  // JSON con configuración

    @Column(name = "last_updated_at")
    private LocalDateTime lastUpdatedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
}