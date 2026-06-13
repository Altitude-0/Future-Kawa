package com.futurekawa.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurekawa.config.MqttConfig;
import com.futurekawa.dto.MeasurementDTO;
import com.futurekawa.entity.Measurement;
import com.futurekawa.entity.Sensor;
import com.futurekawa.mapper.EntityMapper;
import com.futurekawa.repository.SensorRepository;
import com.futurekawa.service.AlertService;
import com.futurekawa.service.MeasurementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttListener {

    private final MeasurementService measurementService;
    private final SensorRepository sensorRepository;
    private final EntityMapper entityMapper;
    private final ObjectMapper objectMapper;
    private final AlertService alertService;

    @RabbitListener(queues = MqttConfig.MEASUREMENTS_QUEUE)
    public void receiveMeasurement(String message) {
        try {
            log.debug("Received measurement message from RabbitMQ: {}", message);

            MeasurementDTO measurementDTO = objectMapper.readValue(message, MeasurementDTO.class);

            if (measurementDTO.getSensorReference() == null) {
                log.warn("Measurement message missing sensorReference: {}", message);
                return;
            }

            Sensor sensor = sensorRepository.findByReference(measurementDTO.getSensorReference())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Sensor not found with reference: " + measurementDTO.getSensorReference()));

            Measurement measurement = entityMapper.toMeasurementEntity(measurementDTO, sensor);
            measurementService.createMeasurement(measurement);

            log.info("Measurement created successfully from RabbitMQ for sensor: {}",
                measurementDTO.getSensorReference());

            // Évalue les alertes (température) pour le conteneur rattaché à ce capteur.
            alertService.evaluateAlertsForSensor(sensor.getId());
        } catch (IllegalArgumentException e) {
            log.warn("Validation error processing measurement: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing measurement message: {}", message, e);
        }
    }
}
