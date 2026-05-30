package com.futurekawa.repository;

import com.futurekawa.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, UUID> {

    List<Measurement> findBySensorIdOrderByCreatedAtDesc(UUID sensorId);

    @Query("""
        SELECT m FROM Measurement m
        WHERE m.sensor.id = :sensorId
        ORDER BY m.createdAt DESC
        LIMIT 1
    """)
    Optional<Measurement> findLatestBySensorId(UUID sensorId);
}
