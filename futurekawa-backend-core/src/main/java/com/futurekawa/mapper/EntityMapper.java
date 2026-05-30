package com.futurekawa.mapper;

import com.futurekawa.dto.*;
import com.futurekawa.entity.*;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public CountryDTO toCountryDTO(Country country) {
        if (country == null) return null;
        return CountryDTO.builder()
            .id(country.getId())
            .codeIso(country.getCodeIso())
            .build();
    }

    public WarehouseDTO toWarehouseDTO(Warehouse warehouse) {
        if (warehouse == null) return null;
        return WarehouseDTO.builder()
            .id(warehouse.getId())
            .name(warehouse.getName())
            .countryId(warehouse.getCountry() != null ? warehouse.getCountry().getId() : null)
            .build();
    }

    public SensorTypeDTO toSensorTypeDTO(SensorType type) {
        if (type == null) return null;
        return SensorTypeDTO.builder()
            .id(type.getId())
            .sensorType(type.getSensorType())
            .build();
    }

    public SensorDTO toSensorDTO(Sensor sensor) {
        if (sensor == null) return null;
        return SensorDTO.builder()
            .id(sensor.getId())
            .sensorTypeId(sensor.getSensorType() != null ? sensor.getSensorType().getId() : null)
            .sensorType(toSensorTypeDTO(sensor.getSensorType()))
            .entryDate(sensor.getEntryDate())
            .reference(sensor.getReference())
            .build();
    }

    public ContainerDTO toContainerDTO(Container container) {
        if (container == null) return null;
        return ContainerDTO.builder()
            .id(container.getId())
            .warehouseId(container.getWarehouse() != null ? container.getWarehouse().getId() : null)
            .warehouse(toWarehouseDTO(container.getWarehouse()))
            .idSensor(container.getSensor() != null ? container.getSensor().getId() : null)
            .sensor(toSensorDTO(container.getSensor()))
            .status(container.getStatus() != null ? container.getStatus().toString() : null)
            .entryDate(container.getEntryDate())
            .exitDate(container.getExitDate())
            .build();
    }

    public MeasurementDTO toMeasurementDTO(Measurement measurement) {
        if (measurement == null) return null;
        return MeasurementDTO.builder()
            .id(measurement.getId())
            .sensorId(measurement.getSensor() != null ? measurement.getSensor().getId() : null)
            .sensor(toSensorDTO(measurement.getSensor()))
            .createdAt(measurement.getCreatedAt())
            .temperature(measurement.getTemperature())
            .humidity(measurement.getHumidity())
            .build();
    }

    public Measurement toMeasurementEntity(MeasurementDTO dto, Sensor sensor) {
        if (dto == null) return null;
        return Measurement.builder()
            .id(dto.getId())
            .sensor(sensor)
            .temperature(dto.getTemperature())
            .humidity(dto.getHumidity())
            .createdAt(dto.getCreatedAt() != null ? dto.getCreatedAt() : java.time.LocalDateTime.now())
            .build();
    }

    public AlertDTO toAlertDTO(Alert alert) {
        if (alert == null) return null;
        return AlertDTO.builder()
            .id(alert.getId())
            .containerId(alert.getContainer() != null ? alert.getContainer().getId() : null)
            .container(toContainerDTO(alert.getContainer()))
            .alertedAt(alert.getAlertedAt())
            .createdAt(alert.getCreatedAt())
            .emailSent(alert.getEmailSent())
            .type(alert.getType() != null ? alert.getType().toString() : null)
            .build();
    }

    public ConfigurationDTO toConfigurationDTO(Configuration config) {
        if (config == null) return null;
        return ConfigurationDTO.builder()
            .id(config.getId())
            .countryId(config.getCountry() != null ? config.getCountry().getId() : null)
            .temperatureIdeal(config.getTemperatureIdeal())
            .humidityIdeal(config.getHumidityIdeal())
            .temperatureTolerance(config.getTemperatureTolerance())
            .humidityTolerance(config.getHumidityTolerance())
            .temperatureUnit(config.getTemperatureUnit())
            .createdAt(config.getCreatedAt())
            .build();
    }

    public Configuration toConfigurationEntity(ConfigurationDTO dto, Country country) {
        if (dto == null) return null;
        return Configuration.builder()
            .id(dto.getId())
            .country(country)
            .temperatureIdeal(dto.getTemperatureIdeal())
            .humidityIdeal(dto.getHumidityIdeal())
            .temperatureTolerance(dto.getTemperatureTolerance())
            .humidityTolerance(dto.getHumidityTolerance())
            .temperatureUnit(dto.getTemperatureUnit())
            .build();
    }

    public ConfigurationAuditDTO toConfigurationAuditDTO(ConfigurationAudit audit) {
        if (audit == null) return null;
        return ConfigurationAuditDTO.builder()
            .id(audit.getId())
            .changedAt(audit.getChangedAt())
            .configurationId(audit.getConfiguration() != null ? audit.getConfiguration().getId() : null)
            .fieldName(audit.getFieldName())
            .newValue(audit.getNewValue())
            .oldValue(audit.getOldValue())
            .userId(audit.getUser() != null ? audit.getUser().getId() : null)
            .build();
    }
}
