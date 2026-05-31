package com.futurekawa.service;

import com.futurekawa.entity.Container;
import com.futurekawa.entity.Sensor;
import com.futurekawa.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SensorService {

    private final SensorRepository sensorRepository;

    public Sensor createSensor(Sensor sensor) {
        if (sensor.getEntryDate() == null) {
            sensor.setEntryDate(LocalDateTime.now());
        }
        return sensorRepository.save(sensor);
    }

    public Sensor getSensorById(UUID id) {
        return sensorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Sensor not found: " + id));
    }

    public List<Sensor> getAllSensors() {
        return sensorRepository.findAll();
    }

    /**
     * Composes the sensor reference according to Modification.md:
     * code_iso_country / name_warehouse / UUID_container
     */
    public String generateReference(Sensor sensor) {
        Container container = sensor.getContainer();
        if (container == null || container.getWarehouse() == null || container.getWarehouse().getCountry() == null) {
            return "UNKNOWN/UNKNOWN/" + (container != null ? container.getId() : "NO_CONTAINER");
        }
        return String.format("%s / %s / %s",
            container.getWarehouse().getCountry().getCodeIso(),
            container.getWarehouse().getName(),
            container.getId().toString());
    }

    public Sensor updateSensorReference(UUID sensorId) {
        Sensor sensor = getSensorById(sensorId);
        sensor.setReference(generateReference(sensor));
        return sensorRepository.save(sensor);
    }
}
