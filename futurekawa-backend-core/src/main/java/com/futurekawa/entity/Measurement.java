package com.futurekawa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "measurements", indexes = {
    @Index(name = "idx_measurement_sensor", columnList = "fk_sensors"),
    @Index(name = "idx_measured_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Measurement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_sensors", nullable = false)
    private Sensor sensor;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private Float temperature;

    @Column(nullable = false)
    private Float humidity;
}
