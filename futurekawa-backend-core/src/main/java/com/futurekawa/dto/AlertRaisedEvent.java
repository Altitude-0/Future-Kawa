package com.futurekawa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Événement publié par backend-core vers la queue "alerts" lorsqu'une alerte
 * de température est levée. Il porte toutes les données nécessaires à la
 * construction du mail : le service de notification n'a donc besoin d'aucune
 * base de données ni d'aucun seuil.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
