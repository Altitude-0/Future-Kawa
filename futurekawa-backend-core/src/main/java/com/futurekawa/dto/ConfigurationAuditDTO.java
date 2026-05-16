package com.futurekawa.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConfigurationAuditDTO {

    private UUID id;

    private String fieldName;

    private String oldValue;

    private String newValue;

    private String changedBy;

    private LocalDateTime changedAt;
}
