package com.futurekawa.repository;

import com.futurekawa.entity.SensorType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface SensorTypeRepository extends JpaRepository<SensorType, UUID> {
    Optional<SensorType> findBySensorType(String sensorType);
}
