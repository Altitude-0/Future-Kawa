package com.futurekawa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "sensors_type")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SensorType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "sensor_type", nullable = false)
    private String type;
}
