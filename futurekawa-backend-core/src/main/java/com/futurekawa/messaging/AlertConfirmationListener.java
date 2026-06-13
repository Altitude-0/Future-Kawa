package com.futurekawa.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurekawa.config.MqttConfig;
import com.futurekawa.dto.AlertSentEvent;
import com.futurekawa.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consomme les confirmations d'envoi de mail émises par le service de notification
 * et marque l'alerte correspondante comme envoyée (emailSent = true). Cette boucle
 * de retour par message évite tout couplage synchrone entre les deux services.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertConfirmationListener {

    private final AlertService alertService;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = MqttConfig.ALERT_CONFIRMATIONS_QUEUE)
    public void receiveConfirmation(String message) {
        try {
            AlertSentEvent event = objectMapper.readValue(message, AlertSentEvent.class);

            if (event.getAlertId() == null) {
                log.warn("AlertSentEvent missing alertId: {}", message);
                return;
            }

            alertService.markAlertAsSent(event.getAlertId());
            log.info("Alert {} marked as sent (email confirmation received)", event.getAlertId());
        } catch (IllegalArgumentException e) {
            log.warn("Confirmation for unknown alert: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing alert confirmation message: {}", message, e);
        }
    }
}
