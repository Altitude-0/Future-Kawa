package com.futurekawa.strategy;

import com.futurekawa.config.FuturekawaProperties;
import com.futurekawa.entity.*;
import com.futurekawa.repository.MeasurementRepository;
import com.futurekawa.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DefaultAlertingStrategy implements AlertingStrategy {

    private final MeasurementRepository measurementRepository;
    private final ConfigurationService configurationService;
    private final FuturekawaProperties properties;

    @Override
    public List<Alert> evaluateAlerts(Container container) {
        List<Alert> alerts = new ArrayList<>();

        // Retrieve dynamic configuration for the container's country
        String countryCode = container.getWarehouse().getCountry().getCodeIso();
        Configuration config = configurationService.getConfiguration(countryCode);

        // Check 1: Container expiry (1 year according to Modification.md)
        long daysInStorage = ChronoUnit.DAYS.between(container.getEntryDate(), LocalDateTime.now());
        if (daysInStorage > 365) {
            Alert expiryAlert = Alert.builder()
                .container(container)
                .alertedAt(LocalDateTime.now())
                .type(Alert.AlertType.OUTDATED_CONTAINER)
                .emailSent(false)
                .build();
            alerts.add(expiryAlert);
        }

        // Check 2: Conditions out of range
        if (container.getSensor() != null) {
            Optional<Measurement> latestMeasurement = measurementRepository.findLatestBySensorId(container.getSensor().getId());
            if (latestMeasurement.isPresent()) {
                Measurement measurement = latestMeasurement.get();
                
                // Temperature check
                Float tempIdeal = config.getTemperatureIdeal();
                Float tempTolerance = config.getTemperatureTolerance();
                if (tempIdeal != null && tempTolerance != null) {
                    if (Math.abs(measurement.getTemperature() - tempIdeal) > tempTolerance) {
                        alerts.add(Alert.builder()
                            .container(container)
                            .alertedAt(LocalDateTime.now())
                            .type(Alert.AlertType.TEMPERATURE_OUT_OF_RANGE)
                            .emailSent(false)
                            .build());
                    }
                }

                // Humidity check
                Float humIdeal = config.getHumidityIdeal();
                Float humTolerance = config.getHumidityTolerance();
                if (humIdeal != null && humTolerance != null) {
                    if (Math.abs(measurement.getHumidity() - humIdeal) > humTolerance) {
                        alerts.add(Alert.builder()
                            .container(container)
                            .alertedAt(LocalDateTime.now())
                            .type(Alert.AlertType.HUMIDITY_OUT_OF_RANGE)
                            .emailSent(false)
                            .build());
                    }
                }
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
    public Float getTemperatureTolerance() {
        return properties.getTemperatureTolerance();
    }

    @Override
    public Float getHumidityTolerance() {
        return properties.getHumidityTolerance();
    }
}
