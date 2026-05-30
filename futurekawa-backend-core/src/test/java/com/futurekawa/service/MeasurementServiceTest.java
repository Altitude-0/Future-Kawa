package com.futurekawa.service;

import com.futurekawa.entity.*;
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
    private Sensor testSensor;
    private UUID testId;
    private UUID sensorId;

    @BeforeEach
    void setUp() {
        testId = UUID.randomUUID();
        sensorId = UUID.randomUUID();

        testSensor = Sensor.builder()
            .id(sensorId)
            .reference("BR/Warehouse1/Container1")
            .entryDate(LocalDateTime.now())
            .build();

        testMeasurement = Measurement.builder()
            .id(testId)
            .sensor(testSensor)
            .createdAt(LocalDateTime.now())
            .temperature(22.5f)
            .humidity(60.0f)
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
        verify(measurementRepository, times(1)).findById(testId);
    }

    @Test
    void testGetAllMeasurements_Success() {
        Measurement measurement2 = Measurement.builder()
            .id(UUID.randomUUID())
            .sensor(testSensor)
            .temperature(23.0f)
            .humidity(65.0f)
            .build();

        List<Measurement> measurements = Arrays.asList(testMeasurement, measurement2);
        when(measurementRepository.findAll()).thenReturn(measurements);

        List<Measurement> result = measurementService.getAllMeasurements();

        assertThat(result).hasSize(2);
        assertThat(result).contains(testMeasurement, measurement2);
        verify(measurementRepository, times(1)).findAll();
    }

    @Test
    void testGetMeasurementsBySensorId_Success() {
        List<Measurement> measurements = Arrays.asList(testMeasurement);
        when(measurementRepository.findBySensorIdOrderByCreatedAtDesc(sensorId)).thenReturn(measurements);

        List<Measurement> result = measurementService.getMeasurementsBySensorId(sensorId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getSensor().getId()).isEqualTo(sensorId);
        verify(measurementRepository, times(1)).findBySensorIdOrderByCreatedAtDesc(sensorId);
    }

    @Test
    void testDeleteMeasurement_Success() {
        when(measurementRepository.existsById(testId)).thenReturn(true);

        measurementService.deleteMeasurement(testId);

        verify(measurementRepository, times(1)).existsById(testId);
        verify(measurementRepository, times(1)).deleteById(testId);
    }
}
