package com.futurekawa.mapper;

import com.futurekawa.dto.AlertDTO;
import com.futurekawa.dto.MeasurementDTO;
import com.futurekawa.dto.StockDTO;
import com.futurekawa.dto.WarehouseDTO;
import com.futurekawa.entity.Alert;
import com.futurekawa.entity.Measurement;
import com.futurekawa.entity.Stock;
import com.futurekawa.entity.Warehouse;
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
}
