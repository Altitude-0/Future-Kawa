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
    private static final Pattern TOPIC_PATTERN = Pattern.compile("stocks/([^/]+)/temperature");

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleTemperatureMessage(Message<?> message) {
        try {
            String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
            String payload = message.getPayload().toString();

            log.debug("Received temperature message from Mosquitto - Topic: {}, Payload: {}", topic, payload);

            String stockId = extractStockId(topic);
            if (stockId == null) {
                log.warn("Could not extract stockId from topic: {}", topic);
                return;
            }

            double temperature = Double.parseDouble(payload);
            Map<String, Object> measurementData = createMeasurementData(stockId, temperature);

            String jsonMessage = objectMapper.writeValueAsString(measurementData);
            rabbitTemplate.convertAndSend(RabbitConfig.MEASUREMENTS_QUEUE, jsonMessage);

            log.info("Temperature message published to RabbitMQ - StockId: {}, Temp: {}°C",
                stockId, temperature);
        } catch (NumberFormatException e) {
            log.warn("Invalid temperature value in MQTT message: {}", message.getPayload(), e);
        } catch (Exception e) {
            log.error("Error processing MQTT temperature message", e);
        }
    }

    private String extractStockId(String topic) {
        if (topic == null) {
            return null;
        }
        Matcher matcher = TOPIC_PATTERN.matcher(topic);
        return matcher.find() ? matcher.group(1) : null;
    }

    private Map<String, Object> createMeasurementData(String stockId, double temperature) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", UUID.randomUUID().toString());
        data.put("stockId", stockId);
        data.put("temperature", temperature);
        data.put("measuredAt", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        data.put("sensorId", "mqtt-sensor");
        return data;
    }
}
