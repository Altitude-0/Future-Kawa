package com.futurekawa.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurekawa.config.FuturekawaProperties;
import com.futurekawa.config.MqttConfig;
import com.futurekawa.dto.AlertRaisedEvent;
import com.futurekawa.entity.Alert;
import com.futurekawa.entity.Configuration;
import com.futurekawa.entity.Container;
import com.futurekawa.repository.MeasurementRepository;
import com.futurekawa.service.ConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publie les événements d'alerte de température vers la queue RabbitMQ "alerts".
 * L'événement embarque toutes les données nécessaires (mesure, seuils, destinataire)
 * pour que le service de notification reste totalement découplé (sans BDD).
 *
 * <p>MVP : la publication est faite dans la transaction de l'évaluation. Une
 * amélioration consisterait à publier en {@code @TransactionalEventListener(AFTER_COMMIT)}
 * pour éviter toute alerte « fantôme » en cas de rollback.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AlertEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final ConfigurationService configurationService;
    private final MeasurementRepository measurementRepository;
    private final FuturekawaProperties properties;

    /**
     * Construit et publie un {@link AlertRaisedEvent} pour une alerte de température.
     * À appeler dans une transaction active (accès lazy à warehouse/country).
     */
    public void publishTemperatureAlert(Alert alert) {
        Container container = alert.getContainer();
        String countryCode = container.getWarehouse().getCountry().getCodeIso();
        Configuration config = configurationService.getConfiguration(countryCode);

        Float measuredTemperature = container.getSensor() == null ? null
            : measurementRepository.findLatestBySensorId(container.getSensor().getId())
                .map(m -> m.getTemperature())
                .orElse(null);

        AlertRaisedEvent event = AlertRaisedEvent.builder()
            .alertId(alert.getId())
            .type(alert.getType().name())
            .containerReference(container.getReference())
            .warehouseName(container.getWarehouse().getName())
            .countryCode(countryCode)
            .measuredTemperature(measuredTemperature)
            .idealTemperature(config.getTemperatureIdeal())
            .tolerance(config.getTemperatureTolerance())
            .temperatureUnit(config.getTemperatureUnit())
            .alertedAt(alert.getAlertedAt())
            .recipient(properties.getAlert().getRecipient())
            .build();

        try {
            String json = objectMapper.writeValueAsString(event);
            rabbitTemplate.convertAndSend(MqttConfig.ALERTS_QUEUE, json);
            log.info("AlertRaisedEvent published for alert {} (container {})",
                alert.getId(), container.getReference());
        } catch (Exception e) {
            log.error("Failed to publish AlertRaisedEvent for alert {}", alert.getId(), e);
        }
    }
}
