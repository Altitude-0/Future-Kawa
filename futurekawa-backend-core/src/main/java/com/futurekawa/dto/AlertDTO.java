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
public class AlertDTO {
    private UUID id;
    private UUID stockId;
    private LocalDateTime alertedAt;
    private String type;
    private String description;
    private Boolean emailSent;
    private LocalDateTime createdAt;
}
