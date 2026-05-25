package com.futurekawa.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.futurekawa.config.MqttConfig;
import com.futurekawa.dto.MeasurementDTO;
import com.futurekawa.entity.Measurement;
import com.futurekawa.entity.Stock;
import com.futurekawa.mapper.EntityMapper;
import com.futurekawa.repository.StockRepository;
import com.futurekawa.service.MeasurementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class MqttListener {

    private final MeasurementService measurementService;
    private final StockRepository stockRepository;
    private final EntityMapper entityMapper;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = MqttConfig.MEASUREMENTS_QUEUE)
    public void receiveMeasurement(String message) {
        try {
            log.debug("Received measurement message from RabbitMQ: {}", message);

            MeasurementDTO measurementDTO = objectMapper.readValue(message, MeasurementDTO.class);

            if (measurementDTO.getStockId() == null) {
                log.warn("Measurement message missing stockId: {}", message);
                return;
            }

            Stock stock = stockRepository.findById(measurementDTO.getStockId())
                .orElseThrow(() -> new IllegalArgumentException(
                    "Stock not found with ID: " + measurementDTO.getStockId()));

            Measurement measurement = entityMapper.toMeasurementEntity(measurementDTO, stock);
            measurementService.createMeasurement(measurement);

            log.info("Measurement created successfully from RabbitMQ for stock: {}",
                measurementDTO.getStockId());
        } catch (IllegalArgumentException e) {
            log.warn("Validation error processing measurement: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing measurement message: {}", message, e);
        }
    }
}
