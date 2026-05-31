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
public class MeasurementDTO {
    private UUID id;
    private String sensorReference;
    private SensorDTO sensor;
    private LocalDateTime createdAt;
    private Float temperature;
    private Float humidity;
}
