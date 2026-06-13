package com.futurekawa.alerting.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Copie du contrat d'événement émis par backend-core sur la queue "alerts".
 * Volontairement dupliqué : ce service reste indépendant et n'a aucune
 * dépendance vers backend-core.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertRaisedEvent {

    private UUID alertId;
    private String type;
    private String containerReference;
    private String warehouseName;
    private String countryCode;
    private Float measuredTemperature;
    private Float idealTemperature;
    private Float tolerance;
    private String temperatureUnit;
    private LocalDateTime alertedAt;
    private String recipient;
}
