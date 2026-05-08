package com.futurekawa.controller;

import com.futurekawa.dto.MeasurementDTO;
import com.futurekawa.entity.Measurement;
import com.futurekawa.mapper.EntityMapper;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/measurements")
@RequiredArgsConstructor
@Tag(name = "Measurements", description = "Temperature and humidity measurement endpoints")
public class MeasurementController {

    private final MeasurementService measurementService;
    private final EntityMapper mapper;

    @PostMapping
    @Operation(summary = "Record a new temperature/humidity measurement")
    public ResponseEntity<MeasurementDTO> createMeasurement(@Valid @RequestBody Measurement measurement) {
        Measurement created = measurementService.createMeasurement(measurement);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toMeasurementDTO(created));
    }

    @GetMapping
    @Operation(summary = "List all measurements")
    public ResponseEntity<List<MeasurementDTO>> getAllMeasurements() {
        List<Measurement> measurements = measurementService.getAllMeasurements();
        return ResponseEntity.ok(measurements.stream().map(mapper::toMeasurementDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a measurement by ID")
    public ResponseEntity<MeasurementDTO> getMeasurementById(@PathVariable UUID id) {
        return measurementService.getMeasurementById(id)
            .map(m -> ResponseEntity.ok(mapper.toMeasurementDTO(m)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stock/{stockId}")
    @Operation(summary = "Get all measurements for a specific stock")
    public ResponseEntity<List<MeasurementDTO>> getMeasurementsByStock(@PathVariable UUID stockId) {
        List<Measurement> measurements = measurementService.getMeasurementsByStockId(stockId);
        return ResponseEntity.ok(measurements.stream().map(mapper::toMeasurementDTO).collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Replace entire measurement (PUT - full replacement)")
    public ResponseEntity<MeasurementDTO> updateMeasurementFull(
            @PathVariable UUID id,
            @Valid @RequestBody Measurement updatedMeasurement) {
        try {
            Measurement measurement = measurementService.updateMeasurement(id, updatedMeasurement);
            return ResponseEntity.ok(mapper.toMeasurementDTO(measurement));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update measurement (PATCH - partial update)")
    public ResponseEntity<MeasurementDTO> updateMeasurementPartial(
            @PathVariable UUID id,
            @RequestBody Measurement partialMeasurement) {
        try {
            Measurement measurement = measurementService.partialUpdateMeasurement(id, partialMeasurement);
            return ResponseEntity.ok(mapper.toMeasurementDTO(measurement));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
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
