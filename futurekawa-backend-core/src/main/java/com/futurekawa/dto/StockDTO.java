package com.futurekawa.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockDTO {
    private UUID id;
    private UUID warehouseId;
    private WarehouseDTO warehouse;
    private String reference;
    private String status;
    private Float qualityScore;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
