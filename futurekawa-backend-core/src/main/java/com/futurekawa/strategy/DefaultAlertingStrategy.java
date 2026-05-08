package com.futurekawa.strategy;

import com.futurekawa.config.FuturekawaProperties;
import com.futurekawa.entity.Alert;
import com.futurekawa.entity.Measurement;
import com.futurekawa.entity.Stock;
import com.futurekawa.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default alerting strategy implementation.
 * Uses FuturekawaProperties for country-specific thresholds.
 * Can be overridden by country/region-specific implementations.
 */
@Component
@RequiredArgsConstructor
public class DefaultAlertingStrategy implements AlertingStrategy {

    private final MeasurementRepository measurementRepository;
    private final FuturekawaProperties properties;

    @Override
    public List<Alert> evaluateAlerts(Stock stock) {
        List<Alert> alerts = new ArrayList<>();

        // Check 1: Stock expiry
        long daysInStorage = ChronoUnit.DAYS.between(stock.getCreatedAt(), LocalDateTime.now());
        if (daysInStorage > properties.getAlertOldLotDays()) {
            Alert expiryAlert = Alert.builder()
                .stock(stock)
                .alertedAt(LocalDateTime.now())
                .type(Alert.AlertType.EXPIRED_STOCK)
                .description(String.format("Stock stored for %d days (expiry: %d days)", daysInStorage, properties.getAlertOldLotDays()))
                .emailSent(false)
                .build();
            alerts.add(expiryAlert);
        }

        // Check 2: Conditions out of range
        Optional<Measurement> latestMeasurement = measurementRepository.findLatestByStockId(stock.getId());
        if (latestMeasurement.isPresent()) {
            Measurement measurement = latestMeasurement.get();
            Float tempIdeal = getIdealTemperature();
            Float humidityIdeal = getIdealHumidity();
            Float tempTolerance = getTemperanceTolerance();
            Float humidityTolerance = getHumidityTolerance();

            boolean tempOutOfRange = Math.abs(measurement.getTemperature() - tempIdeal) > tempTolerance;
            boolean humidityOutOfRange = Math.abs(measurement.getHumidity() - humidityIdeal) > humidityTolerance;

            if (tempOutOfRange || humidityOutOfRange) {
                Alert conditionAlert = Alert.builder()
                    .stock(stock)
                    .alertedAt(LocalDateTime.now())
                    .type(Alert.AlertType.CONDITION_OUT_OF_RANGE)
                    .description(String.format(
                        "Conditions out of range: T=%.1f°C (ideal %.1f±%.1f), H=%.1f%% (ideal %.1f±%.1f)",
                        measurement.getTemperature(), tempIdeal, tempTolerance,
                        measurement.getHumidity(), humidityIdeal, humidityTolerance
                    ))
                    .emailSent(false)
                    .build();
                alerts.add(conditionAlert);
            }
        }

        return alerts;
    }

    @Override
    public Float getIdealTemperature() {
        return properties.getTemperatureIdeal();
    }

    @Override
    public Float getIdealHumidity() {
        return properties.getHumidityIdeal();
    }

    @Override
    public Float getTemperanceTolerance() {
        return properties.getTemperatureTolerance();
    }

    @Override
    public Float getHumidityTolerance() {
        return properties.getHumidityTolerance();
    }
}
