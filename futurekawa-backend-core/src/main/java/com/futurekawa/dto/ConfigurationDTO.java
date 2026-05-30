package com.futurekawa.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationDTO {
    private UUID id;
    private UUID countryId;
    private Float temperatureIdeal;
    private Float humidityIdeal;
    private Float temperatureTolerance;
    private Float humidityTolerance;
    private String temperatureUnit;
    private LocalDateTime createdAt;
}
