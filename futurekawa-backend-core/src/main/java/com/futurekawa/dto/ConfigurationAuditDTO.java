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
    private LocalDateTime changedAt;
    private UUID configurationId;
    private String fieldName;
    private String newValue;
    private String oldValue;
    private UUID userId;
}
