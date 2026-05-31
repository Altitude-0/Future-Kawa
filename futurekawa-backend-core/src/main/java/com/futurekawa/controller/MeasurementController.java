package com.futurekawa.controller;

import com.futurekawa.dto.MeasurementDTO;
import com.futurekawa.entity.Measurement;
import com.futurekawa.entity.Sensor;
import com.futurekawa.mapper.EntityMapper;
import com.futurekawa.repository.SensorRepository;
import com.futurekawa.service.MeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/measurements")
@RequiredArgsConstructor
@Tag(name = "Measurements", description = "Temperature and humidity measurement endpoints")
public class MeasurementController {

    private final MeasurementService measurementService;
    private final SensorRepository sensorRepository;
    private final EntityMapper mapper;

    @PostMapping
    @Operation(summary = "Record a new measurement")
    public ResponseEntity<MeasurementDTO> createMeasurement(@Valid @RequestBody MeasurementDTO measurementDto) {
        Sensor sensorRef = null;
        if (measurementDto.getSensorReference() != null) {
            sensorRef = sensorRepository.findByReference(measurementDto.getSensorReference())
                .orElseThrow(() -> new IllegalArgumentException("Sensor not found with reference: " + measurementDto.getSensorReference()));
        }
        Measurement measurementToCreate = mapper.toMeasurementEntity(measurementDto, sensorRef);
        Measurement created = measurementService.createMeasurement(measurementToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toMeasurementDTO(created));
    }

    @GetMapping
    @Operation(summary = "List all measurements")
    public ResponseEntity<List<MeasurementDTO>> getAllMeasurements() {
        List<Measurement> measurements = measurementService.getAllMeasurements();
        return ResponseEntity.ok(measurements.stream().map(mapper::toMeasurementDTO).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a measurement by ID")
    public ResponseEntity<MeasurementDTO> getMeasurementById(@PathVariable UUID id) {
        return measurementService.getMeasurementById(id)
            .map(m -> ResponseEntity.ok(mapper.toMeasurementDTO(m)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sensor/{sensorId}")
    @Operation(summary = "Get all measurements for a specific sensor")
    public ResponseEntity<List<MeasurementDTO>> getMeasurementsBySensor(@PathVariable UUID sensorId) {
        List<Measurement> measurements = measurementService.getMeasurementsBySensorId(sensorId);
        return ResponseEntity.ok(measurements.stream().map(mapper::toMeasurementDTO).toList());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a measurement")
    public ResponseEntity<Void> deleteMeasurement(@PathVariable UUID id) {
        try {
            measurementService.deleteMeasurement(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
