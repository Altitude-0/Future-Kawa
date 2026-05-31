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
public class SensorDTO {
    private UUID id;
    private UUID sensorTypeId;
    private SensorTypeDTO sensorType;
    private LocalDateTime entryDate;
    private String reference;
}
