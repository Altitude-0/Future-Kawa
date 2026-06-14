package com.futurekawa.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Table(name = "alerts", indexes = {
    @Index(name = "idx_alert_container", columnList = "fk_containers"),
    @Index(name = "idx_alerted_at", columnList = "alerted_at"),
    @Index(name = "idx_sent", columnList = "email_sent")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_containers", nullable = false)
    private Container container;

    @Column(name = "alerted_at", nullable = false)
    private LocalDateTime alertedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now(ZoneId.systemDefault());

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "email_sent", nullable = false)
    @Builder.Default
    private Boolean emailSent = false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    public enum AlertType {
        TEMPERATURE_OUT_OF_RANGE,
        HUMIDITY_OUT_OF_RANGE,
        OUTDATED_CONTAINER,
        OTHER
    }
}
