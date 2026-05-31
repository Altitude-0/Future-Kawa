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

        evaluateExpiry(container, alerts);
        evaluateConditions(container, config, alerts);

        return alerts;
    }

    private void evaluateExpiry(Container container, List<Alert> alerts) {
        long daysInStorage = ChronoUnit.DAYS.between(container.getEntryDate(), LocalDateTime.now());
        if (daysInStorage > 365) {
            alerts.add(createAlert(container, Alert.AlertType.OUTDATED_CONTAINER));
        }
    }

    private void evaluateConditions(Container container, Configuration config, List<Alert> alerts) {
        if (container.getSensor() == null) {
            return;
        }

        measurementRepository.findLatestBySensorId(container.getSensor().getId())
            .ifPresent(measurement -> {
                evaluateTemperature(container, measurement, config, alerts);
                evaluateHumidity(container, measurement, config, alerts);
            });
    }

    private void evaluateTemperature(Container container, Measurement measurement, Configuration config, List<Alert> alerts) {
        Float tempIdeal = config.getTemperatureIdeal();
        Float tempTolerance = config.getTemperatureTolerance();
        if (tempIdeal != null && tempTolerance != null && Math.abs(measurement.getTemperature() - tempIdeal) > tempTolerance) {
            alerts.add(createAlert(container, Alert.AlertType.TEMPERATURE_OUT_OF_RANGE));
        }
    }

    private void evaluateHumidity(Container container, Measurement measurement, Configuration config, List<Alert> alerts) {
        Float humIdeal = config.getHumidityIdeal();
        Float humTolerance = config.getHumidityTolerance();
        if (humIdeal != null && humTolerance != null && Math.abs(measurement.getHumidity() - humIdeal) > humTolerance) {
            alerts.add(createAlert(container, Alert.AlertType.HUMIDITY_OUT_OF_RANGE));
        }
    }

    private Alert createAlert(Container container, Alert.AlertType type) {
        return Alert.builder()
            .container(container)
            .alertedAt(LocalDateTime.now())
            .type(type)
            .emailSent(false)
            .build();
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
