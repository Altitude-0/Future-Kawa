package com.futurekawa.service;

import com.futurekawa.entity.Measurement;
import com.futurekawa.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MeasurementService {

    private final MeasurementRepository measurementRepository;

    public Measurement createMeasurement(Measurement measurement) {
        measurement.setCreatedAt(LocalDateTime.now());
        return measurementRepository.save(measurement);
    }

    public Optional<Measurement> getMeasurementById(UUID id) {
        return measurementRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Measurement> getAllMeasurements() {
        return measurementRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Measurement> getMeasurementsByStockId(UUID stockId) {
        return measurementRepository.findByStockIdOrderByMeasuredAtDesc(stockId);
    }

    public Measurement updateMeasurement(UUID id, Measurement updatedMeasurement) {
        Measurement measurement = measurementRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Measurement not found: " + id));
        measurement.setTemperature(updatedMeasurement.getTemperature());
        measurement.setHumidity(updatedMeasurement.getHumidity());
        measurement.setMeasuredAt(updatedMeasurement.getMeasuredAt());
        return measurementRepository.save(measurement);
    }

    public Measurement partialUpdateMeasurement(UUID id, Measurement partialMeasurement) {
        Measurement measurement = measurementRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Measurement not found: " + id));
        if (partialMeasurement.getTemperature() != null) {
            measurement.setTemperature(partialMeasurement.getTemperature());
        }
        if (partialMeasurement.getHumidity() != null) {
            measurement.setHumidity(partialMeasurement.getHumidity());
        }
        if (partialMeasurement.getMeasuredAt() != null) {
            measurement.setMeasuredAt(partialMeasurement.getMeasuredAt());
        }
        return measurementRepository.save(measurement);
    }

    public void deleteMeasurement(UUID id) {
        if (!measurementRepository.existsById(id)) {
            throw new IllegalArgumentException("Measurement not found: " + id);
        }
        measurementRepository.deleteById(id);
    }
}
