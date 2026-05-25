package com.futurekawa.strategy;

import com.futurekawa.config.FuturekawaProperties;
import com.futurekawa.entity.Alert;
import com.futurekawa.entity.Configuration;
import com.futurekawa.entity.Measurement;
import com.futurekawa.entity.Stock;
import com.futurekawa.repository.MeasurementRepository;
import com.futurekawa.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Default alerting strategy implementation.
 * Uses ConfigurationService for dynamic country-specific thresholds.
 * Falls back to FuturekawaProperties if configuration not found.
 * Can be overridden by country/region-specific implementations.
 */
@Component
@RequiredArgsConstructor
public class DefaultAlertingStrategy implements AlertingStrategy {

    private final MeasurementRepository measurementRepository;
    private final ConfigurationService configurationService;
    private final FuturekawaProperties properties;

    @Override
    public List<Alert> evaluateAlerts(Stock stock) {
        List<Alert> alerts = new ArrayList<>();

        // Retrieve dynamic configuration for the stock's country
        String countryCode = stock.getWarehouse().getCountry().getCode();
        Configuration config = configurationService.getConfiguration(countryCode);

        // Check 1: Stock expiry
        long daysInStorage = ChronoUnit.DAYS.between(stock.getCreatedAt(), LocalDateTime.now());
        if (daysInStorage > config.getAlertOldLotDays()) {
            Alert expiryAlert = Alert.builder()
                .stock(stock)
                .alertedAt(LocalDateTime.now())
                .type(Alert.AlertType.EXPIRED_STOCK)
                .description(String.format("Stock stored for %d days (expiry: %d days)", daysInStorage, config.getAlertOldLotDays()))
                .emailSent(false)
                .build();
            alerts.add(expiryAlert);
        }

        // Check 2: Conditions out of range
        Optional<Measurement> latestMeasurement = measurementRepository.findLatestByStockId(stock.getId());
        if (latestMeasurement.isPresent()) {
            Measurement measurement = latestMeasurement.get();
            Float tempIdeal = config.getTemperatureIdeal();
            Float tempTolerance = config.getTemperatureTolerance();

            boolean tempOutOfRange = Math.abs(measurement.getTemperature() - tempIdeal) > tempTolerance;

            if (tempOutOfRange) {
                Alert conditionAlert = Alert.builder()
                    .stock(stock)
                    .alertedAt(LocalDateTime.now())
                    .type(Alert.AlertType.CONDITION_OUT_OF_RANGE)
                    .description(String.format(
                        "Temperature out of range: %.1f°C (ideal %.1f±%.1f)",
                        measurement.getTemperature(), tempIdeal, tempTolerance
                    ))
                    .emailSent(false)
                    .build();
                alerts.add(conditionAlert);
            }
        }

        return alerts;
    }

    /**
     * Fallback methods returning default properties values.
     * These are only used if evaluateAlerts cannot retrieve configuration.
     */
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
