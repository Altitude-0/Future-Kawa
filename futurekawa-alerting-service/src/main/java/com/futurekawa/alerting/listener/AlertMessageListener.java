package com.futurekawa.alerting.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurekawa.alerting.config.RabbitConfig;
import com.futurekawa.alerting.dto.AlertRaisedEvent;
import com.futurekawa.alerting.dto.AlertSentEvent;
import com.futurekawa.alerting.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalDateTime;

/**
 * Consomme les événements d'alerte, envoie le mail puis publie une confirmation.
 * La confirmation n'est émise que si l'envoi SMTP a réussi : en cas d'échec,
 * l'exception remonte et le message reste à retraiter par RabbitMQ.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertMessageListener {

    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final Clock clock;

    @RabbitListener(queues = RabbitConfig.ALERTS_QUEUE)
    public void handleAlert(String message) throws Exception {
        log.debug("Received alert event: {}", message);

        AlertRaisedEvent event = objectMapper.readValue(message, AlertRaisedEvent.class);

        if (event.getRecipient() == null || event.getAlertId() == null) {
            log.warn("Alert event missing recipient or alertId, skipping: {}", message);
            return;
        }

        emailService.sendTemperatureAlert(event);
        publishConfirmation(event);
    }

    private void publishConfirmation(AlertRaisedEvent event) throws Exception {
        AlertSentEvent confirmation = AlertSentEvent.builder()
            .alertId(event.getAlertId())
            .sentAt(LocalDateTime.now(clock))
            .build();

        String json = objectMapper.writeValueAsString(confirmation);
        rabbitTemplate.convertAndSend(RabbitConfig.ALERT_CONFIRMATIONS_QUEUE, json);
        log.info("Sent confirmation published for alert {}", event.getAlertId());
    }
}
