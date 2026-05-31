package com.futurekawa.mqtt.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurekawa.mqtt.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class MosquittoMeasurementListener {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private static final Pattern TOPIC_PATTERN = Pattern.compile("sensors/([^/]+)/metrics");
    private static final String FIELD_TEMPERATURE = "temperature";
    private static final String FIELD_HUMIDITY = "humidity";

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        try {
            String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
            String payload = message.getPayload().toString();

            log.debug("Received message from Mosquitto - Topic: {}, Payload: {}", topic, payload);

            String sensorReference = extractSensorId(topic);
            if (sensorReference == null) {
                log.warn("Could not extract sensorReference from topic: {}", topic);
                return;
            }

            // Expecting JSON payload: {"temperature": 22.5, "humidity": 60.0}
            Map<String, Object> payloadMap = objectMapper.readValue(payload, Map.class);
            
            Map<String, Object> measurementData = new HashMap<>();
            measurementData.put("id", UUID.randomUUID().toString());
            measurementData.put("sensorReference", sensorReference);
            measurementData.put(FIELD_TEMPERATURE, payloadMap.get(FIELD_TEMPERATURE));
            measurementData.put(FIELD_HUMIDITY, payloadMap.get(FIELD_HUMIDITY));
            measurementData.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

            String jsonMessage = objectMapper.writeValueAsString(measurementData);
            rabbitTemplate.convertAndSend(RabbitConfig.MEASUREMENTS_QUEUE, jsonMessage);

            log.info("Measurement published to RabbitMQ - SensorRef: {}, Temp: {}, Hum: {}",
                sensorReference, payloadMap.get(FIELD_TEMPERATURE), payloadMap.get(FIELD_HUMIDITY));
        } catch (Exception e) {
            log.error("Error processing MQTT message", e);
        }
    }

    private String extractSensorId(String topic) {
        if (topic == null) return null;
        Matcher matcher = TOPIC_PATTERN.matcher(topic);
        return matcher.find() ? matcher.group(1) : null;
    }
}
