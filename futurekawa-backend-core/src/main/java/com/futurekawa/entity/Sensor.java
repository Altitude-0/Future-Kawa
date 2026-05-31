package com.futurekawa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sensors")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_sensor_type", nullable = false)
    private SensorType sensorType;

    @Column(name = "entry_date", nullable = false)
    private LocalDateTime entryDate;

    @Column(nullable = false, unique = true)
    private String reference;

    @OneToOne(mappedBy = "sensor")
    private Container container;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<Measurement> measurements;
}
