package com.futurekawa.mapper;

import com.futurekawa.dto.*;
import com.futurekawa.entity.*;
import org.springframework.stereotype.Component;

@Component
public class EntityMapper {

    public WarehouseDTO toWarehouseDTO(Warehouse warehouse) {
        if (warehouse == null) {
            return null;
        }
        return WarehouseDTO.builder()
            .id(warehouse.getId())
            .name(warehouse.getName())
            .idealTemperature(warehouse.getIdealTemperature())
            .idealHumidity(warehouse.getIdealHumidity())
            .toleranceTemperature(warehouse.getToleranceTemperature())
            .toleranceHumidity(warehouse.getToleranceHumidity())
            .build();
    }

    public StockDTO toStockDTO(Stock stock) {
        if (stock == null) {
            return null;
        }
        return StockDTO.builder()
            .id(stock.getId())
            .warehouseId(stock.getWarehouse() != null ? stock.getWarehouse().getId() : null)
            .warehouse(toWarehouseDTO(stock.getWarehouse()))
            .reference(stock.getReference())
            .status(stock.getStatus().toString())
            .qualityScore(stock.getQualityScore())
            .createdAt(stock.getCreatedAt())
            .updatedAt(stock.getUpdatedAt())
            .build();
    }

    public MeasurementDTO toMeasurementDTO(Measurement measurement) {
        if (measurement == null) {
            return null;
        }
        return MeasurementDTO.builder()
            .id(measurement.getId())
            .stockId(measurement.getStock() != null ? measurement.getStock().getId() : null)
            .measuredAt(measurement.getMeasuredAt())
            .temperature(measurement.getTemperature())
            .humidity(measurement.getHumidity())
            .sensorId(measurement.getSensorId())
            .createdAt(measurement.getCreatedAt())
            .build();
    }

    public AlertDTO toAlertDTO(Alert alert) {
        if (alert == null) {
            return null;
        }
        return AlertDTO.builder()
            .id(alert.getId())
            .stockId(alert.getStock() != null ? alert.getStock().getId() : null)
            .alertedAt(alert.getAlertedAt())
            .type(alert.getType() != null ? alert.getType().toString() : null)
            .description(alert.getDescription())
            .emailSent(alert.getEmailSent())
            .createdAt(alert.getCreatedAt())
            .build();
    }

    public ConfigurationDTO toConfigurationDTO(Configuration config) {
        if (config == null) {
            return null;
        }
        return ConfigurationDTO.builder()
            .id(config.getId())
            .countryCode(config.getCountry() != null ? config.getCountry().getCode() : null)
            .temperatureIdeal(config.getTemperatureIdeal())
            .humidityIdeal(config.getHumidityIdeal())
            .temperatureTolerance(config.getTemperatureTolerance())
            .humidityTolerance(config.getHumidityTolerance())
            .temperatureUnit(config.getTemperatureUnit().toString())
            .alertOldLotDays(config.getAlertOldLotDays())
            .createdAt(config.getCreatedAt())
            .updatedAt(config.getUpdatedAt())
            .build();
    }

    public Configuration toConfigurationEntity(ConfigurationDTO dto, Country country) {
        if (dto == null) {
            return null;
        }
        return Configuration.builder()
            .id(dto.getId())
            .country(country)
            .temperatureIdeal(dto.getTemperatureIdeal())
            .humidityIdeal(dto.getHumidityIdeal())
            .temperatureTolerance(dto.getTemperatureTolerance())
            .humidityTolerance(dto.getHumidityTolerance())
            .temperatureUnit(Configuration.TemperatureUnit.valueOf(dto.getTemperatureUnit()))
            .alertOldLotDays(dto.getAlertOldLotDays())
            .build();
    }

    public ConfigurationAuditDTO toConfigurationAuditDTO(ConfigurationAudit audit) {
        if (audit == null) {
            return null;
        }
        return ConfigurationAuditDTO.builder()
            .id(audit.getId())
            .fieldName(audit.getFieldName())
            .oldValue(audit.getOldValue())
            .newValue(audit.getNewValue())
            .changedBy(audit.getUser() != null ? audit.getUser().getUsername() : null)
            .changedAt(audit.getChangedAt())
            .build();
    }
}
