package com.futurekawa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Configuration entity representing country-specific settings for temperature, humidity, and alerting.
 * Each country (BR, EC, CO) has exactly one configuration record with ideal values and tolerances.
 */
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
    @JoinColumn(name = "country_id", nullable = false, unique = true)
    private Country country;

    @Column(name = "temperature_ideal", nullable = false)
    private Float temperatureIdeal;

    @Column(name = "humidity_ideal", nullable = false)
    private Float humidityIdeal;

    @Column(name = "temperature_tolerance", nullable = false)
    private Float temperatureTolerance;

    @Column(name = "humidity_tolerance", nullable = false)
    private Float humidityTolerance;

    @Enumerated(EnumType.STRING)
    @Column(name = "temperature_unit", nullable = false)
    @Builder.Default
    private TemperatureUnit temperatureUnit = TemperatureUnit.CELSIUS;

    @Column(name = "alert_old_lot_days", nullable = false)
    @Builder.Default
    private Integer alertOldLotDays = 365;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum TemperatureUnit {
        CELSIUS,
        FAHRENHEIT
    }
}
