package com.futurekawa.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationDTO {

    private UUID id;

    private String countryCode;

    private Float temperatureIdeal;

    private Float humidityIdeal;

    private Float temperatureTolerance;

    private Float humidityTolerance;

    @JsonProperty("temperatureUnit")
    private String temperatureUnit;

    private Integer alertOldLotDays;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
