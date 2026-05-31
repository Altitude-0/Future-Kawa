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
        if (measurement.getCreatedAt() == null) {
            measurement.setCreatedAt(LocalDateTime.now());
        }
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
    public List<Measurement> getMeasurementsBySensorId(UUID sensorId) {
        return measurementRepository.findBySensorIdOrderByCreatedAtDesc(sensorId);
    }

    public void deleteMeasurement(UUID id) {
        if (!measurementRepository.existsById(id)) {
            throw new IllegalArgumentException("Measurement not found: " + id);
        }
        measurementRepository.deleteById(id);
    }
}
