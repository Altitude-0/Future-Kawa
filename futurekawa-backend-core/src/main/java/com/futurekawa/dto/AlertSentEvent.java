package com.futurekawa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Confirmation renvoyée par le service de notification (queue "alert-confirmations")
 * une fois le mail effectivement envoyé. backend-core l'utilise pour passer
 * Alert.emailSent à true, fermant ainsi la boucle de façon découplée.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertSentEvent {

    private UUID alertId;
    private LocalDateTime sentAt;
}
