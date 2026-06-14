package com.futurekawa.service;

import com.futurekawa.entity.Alert;
import com.futurekawa.entity.Container;
import com.futurekawa.messaging.AlertEventPublisher;
import com.futurekawa.repository.AlertRepository;
import com.futurekawa.repository.ContainerRepository;
import com.futurekawa.strategy.AlertingStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertingStrategy alertingStrategy;
    private final ContainerService containerService;
    private final ContainerRepository containerRepository;
    private final AlertEventPublisher alertEventPublisher;

    /**
     * Point d'entrée appelé après la persistance d'une mesure : résout le conteneur
     * rattaché au capteur puis déclenche l'évaluation des alertes. Tout se fait dans
     * cette transaction pour garantir le chargement lazy (warehouse, country, mesures).
     */
    public void evaluateAlertsForSensor(UUID sensorId) {
        containerRepository.findBySensorId(sensorId)
            .ifPresent(this::evaluateContainerAlerts);
    }

    public void evaluateContainerAlerts(Container container) {
        List<Alert> newAlerts = alertingStrategy.evaluateAlerts(container);

        for (Alert newAlert : newAlerts) {
            // Check if alert already exists
            boolean exists = container.getAlerts().stream()
                .anyMatch(a -> a.getType() == newAlert.getType());

            if (!exists) {
                alertRepository.save(newAlert);
                container.getAlerts().add(newAlert);

                // MVP : seules les alertes de température déclenchent une notification mail.
                if (newAlert.getType() == Alert.AlertType.TEMPERATURE_OUT_OF_RANGE) {
                    alertEventPublisher.publishTemperatureAlert(newAlert);
                }
            }
        }

        // Update container status based on alerts
        if (!newAlerts.isEmpty()) {
            containerService.updateContainerStatus(container.getId(), Container.Status.WARNING);
        }
    }

    public List<Alert> getAlertsByContainer(UUID containerId) {
        return alertRepository.findByContainerIdOrderByAlertedAtDesc(containerId);
    }

    public List<Alert> getUnsentAlerts() {
        return alertRepository.findByEmailSentFalse();
    }

    public void evaluateAllContainers() {
        List<Container> allContainers = containerService.getAllContainers();
        for (Container container : allContainers) {
            evaluateContainerAlerts(container);
        }
    }

    @Transactional(readOnly = true)
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Alert> getAlertById(UUID id) {
        return alertRepository.findById(id);
    }

    public Alert markAlertAsSent(UUID alertId) {
        Alert alert = alertRepository.findById(alertId)
            .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + alertId));
        alert.setEmailSent(true);
        return alertRepository.save(alert);
    }

    public void deleteAlert(UUID id) {
        if (!alertRepository.existsById(id)) {
            throw new IllegalArgumentException("Alert not found: " + id);
        }
        alertRepository.deleteById(id);
    }
}
