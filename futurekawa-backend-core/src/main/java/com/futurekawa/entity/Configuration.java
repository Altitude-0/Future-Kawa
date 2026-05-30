package com.futurekawa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "configurations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Configuration {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_country", nullable = false, unique = true)
    private Country country;

    @Column(name = "temperature_ideal")
    private Float temperatureIdeal;

    @Column(name = "humidity_ideal")
    private Float humidityIdeal;

    @Column(name = "temperature_tolerance")
    private Float temperatureTolerance;

    @Column(name = "humidity_tolerance")
    private Float humidityTolerance;

    @Column(name = "temperature_unit")
    private String temperatureUnit;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
