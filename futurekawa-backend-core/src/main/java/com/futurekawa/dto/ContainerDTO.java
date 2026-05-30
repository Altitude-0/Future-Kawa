package com.futurekawa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContainerDTO {
    private UUID id;
    private UUID warehouseId;
    private WarehouseDTO warehouse;
    private UUID idSensor;
    private SensorDTO sensor;
    private String status;
    private LocalDateTime entryDate;
    private LocalDateTime exitDate;
}
