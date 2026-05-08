package com.futurekawa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "warehouses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private Float idealTemperature;

    @Column(nullable = false)
    private Float idealHumidity;

    @Column(nullable = false)
    private Float toleranceTemperature;

    @Column(nullable = false)
    private Float toleranceHumidity;

    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL)
    private List<Stock> stocks;
}
