package com.futurekawa.service;

import com.futurekawa.entity.Measurement;
import com.futurekawa.entity.Stock;
import com.futurekawa.entity.Warehouse;
import com.futurekawa.repository.MeasurementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MeasurementServiceTest {

    @Mock
    private MeasurementRepository measurementRepository;

    @InjectMocks
    private MeasurementService measurementService;

    private Measurement testMeasurement;
    private Stock testStock;
    private UUID testId;
    private UUID stockId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        stockId = UUID.randomUUID();

        Warehouse warehouse = Warehouse.builder()
            .id(UUID.randomUUID())
            .name("Test Warehouse")
            .build();

        testStock = Stock.builder()
            .id(stockId)
            .warehouse(warehouse)
            .reference("TEST-001")
            .status(Stock.Status.COMPLIANT)
            .build();

        testMeasurement = Measurement.builder()
            .id(testId)
            .stock(testStock)
            .measuredAt(LocalDateTime.now())
            .temperature(22.5f)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    void testCreateMeasurement_Success() {
        when(measurementRepository.save(any(Measurement.class))).thenReturn(testMeasurement);

        Measurement result = measurementService.createMeasurement(testMeasurement);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(testId);
        assertThat(result.getTemperature()).isEqualTo(22.5f);
        verify(measurementRepository, times(1)).save(any(Measurement.class));
    }

    @Test
    void testGetMeasurementById_Success() {
        when(measurementRepository.findById(testId)).thenReturn(Optional.of(testMeasurement));

        Optional<Measurement> result = measurementService.getMeasurementById(testId);

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(testId);
        assertThat(result.get().getTemperature()).isEqualTo(22.5f);
        verify(measurementRepository, times(1)).findById(testId);
    }

    @Test
    void testGetMeasurementById_NotFound() {
        when(measurementRepository.findById(testId)).thenReturn(Optional.empty());

        Optional<Measurement> result = measurementService.getMeasurementById(testId);

        assertThat(result).isEmpty();
        verify(measurementRepository, times(1)).findById(testId);
    }

    @Test
    void testGetAllMeasurements_Success() {
        Measurement measurement2 = Measurement.builder()
            .id(UUID.randomUUID())
            .stock(testStock)
            .measuredAt(LocalDateTime.now())
            .temperature(23.0f)
            .build();

        List<Measurement> measurements = Arrays.asList(testMeasurement, measurement2);
        when(measurementRepository.findAll()).thenReturn(measurements);

        List<Measurement> result = measurementService.getAllMeasurements();

        assertThat(result).hasSize(2);
        assertThat(result).contains(testMeasurement, measurement2);
        verify(measurementRepository, times(1)).findAll();
    }

    @Test
    void testGetMeasurementsByStockId_Success() {
        List<Measurement> measurements = Arrays.asList(testMeasurement);
        when(measurementRepository.findByStockIdOrderByMeasuredAtDesc(stockId)).thenReturn(measurements);

        List<Measurement> result = measurementService.getMeasurementsByStockId(stockId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStock().getId()).isEqualTo(stockId);
        verify(measurementRepository, times(1)).findByStockIdOrderByMeasuredAtDesc(stockId);
    }

    @Test
    void testUpdateMeasurement_Success() {
        Measurement updatedMeasurement = Measurement.builder()
            .id(testId)
            .stock(testStock)
            .measuredAt(LocalDateTime.now())
            .temperature(23.5f)
            .createdAt(testMeasurement.getCreatedAt())
            .build();

        when(measurementRepository.findById(testId)).thenReturn(Optional.of(testMeasurement));
        when(measurementRepository.save(any(Measurement.class))).thenReturn(updatedMeasurement);

        Measurement result = measurementService.updateMeasurement(testId, updatedMeasurement);

        assertThat(result).isNotNull();
        assertThat(result.getTemperature()).isEqualTo(23.5f);
        verify(measurementRepository, times(1)).findById(testId);
        verify(measurementRepository, times(1)).save(any(Measurement.class));
    }

    @Test
    void testPartialUpdateMeasurement_Success() {
        Measurement partialUpdate = Measurement.builder()
            .temperature(24.0f)
            .build();

        when(measurementRepository.findById(testId)).thenReturn(Optional.of(testMeasurement));
        when(measurementRepository.save(any(Measurement.class))).thenReturn(testMeasurement);

        Measurement result = measurementService.partialUpdateMeasurement(testId, partialUpdate);

        assertThat(result).isNotNull();
        assertThat(result.getTemperature()).isEqualTo(24.0f);
        verify(measurementRepository, times(1)).findById(testId);
        verify(measurementRepository, times(1)).save(any(Measurement.class));
    }

    @Test
    void testDeleteMeasurement_Success() {
        when(measurementRepository.existsById(testId)).thenReturn(true);

        measurementService.deleteMeasurement(testId);

        verify(measurementRepository, times(1)).existsById(testId);
        verify(measurementRepository, times(1)).deleteById(testId);
    }

    @Test
    void testDeleteMeasurement_NotFound() {
        when(measurementRepository.existsById(testId)).thenReturn(false);

        assertThatThrownBy(() -> measurementService.deleteMeasurement(testId))
            .isInstanceOf(IllegalArgumentException.class);
        verify(measurementRepository, times(1)).existsById(testId);
    }
}
