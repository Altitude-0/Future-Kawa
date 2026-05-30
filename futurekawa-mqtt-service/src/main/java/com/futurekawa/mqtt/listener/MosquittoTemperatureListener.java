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
public class MosquittoTemperatureListener {

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private static final Pattern TOPIC_PATTERN = Pattern.compile("sensors/([^/]+)/metrics");

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        try {
            String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
            String payload = message.getPayload().toString();

            log.debug("Received message from Mosquitto - Topic: {}, Payload: {}", topic, payload);

            String sensorId = extractSensorId(topic);
            if (sensorId == null) {
                log.warn("Could not extract sensorId from topic: {}", topic);
                return;
            }

            // Expecting JSON payload: {"temperature": 22.5, "humidity": 60.0}
            Map<String, Object> payloadMap = objectMapper.readValue(payload, Map.class);
            
            Map<String, Object> measurementData = new HashMap<>();
            measurementData.put("id", UUID.randomUUID().toString());
            measurementData.put("sensorId", sensorId);
            measurementData.put("temperature", payloadMap.get("temperature"));
            measurementData.put("humidity", payloadMap.get("humidity"));
            measurementData.put("createdAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

            String jsonMessage = objectMapper.writeValueAsString(measurementData);
            rabbitTemplate.convertAndSend(RabbitConfig.MEASUREMENTS_QUEUE, jsonMessage);

            log.info("Measurement published to RabbitMQ - SensorId: {}, Temp: {}, Hum: {}",
                sensorId, payloadMap.get("temperature"), payloadMap.get("humidity"));
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
