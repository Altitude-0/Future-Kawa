package com.futurekawa.alerting.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Confirmation d'envoi renvoyée à backend-core sur la queue "alert-confirmations".
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertSentEvent {

    private UUID alertId;
    private LocalDateTime sentAt;
}
