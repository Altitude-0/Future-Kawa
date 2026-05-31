package com.futurekawa.controller;

import com.futurekawa.dto.AlertDTO;
import com.futurekawa.entity.Alert;
import com.futurekawa.mapper.EntityMapper;
import com.futurekawa.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alerts")
@RequiredArgsConstructor
@Tag(name = "Alerts", description = "Quality and expiry alert endpoints")
public class AlertController {

    private final AlertService alertService;
    private final EntityMapper mapper;

    @GetMapping
    @Operation(summary = "List all alerts")
    public ResponseEntity<List<AlertDTO>> getAllAlerts() {
        List<Alert> alerts = alertService.getAllAlerts();
        return ResponseEntity.ok(alerts.stream().map(mapper::toAlertDTO).toList());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an alert by ID")
    public ResponseEntity<AlertDTO> getAlertById(@PathVariable UUID id) {
        return alertService.getAlertById(id)
            .map(alert -> ResponseEntity.ok(mapper.toAlertDTO(alert)))
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/container/{containerId}")
    @Operation(summary = "Get all alerts for a specific container")
    public ResponseEntity<List<AlertDTO>> getAlertsByContainer(@PathVariable UUID containerId) {
        List<Alert> alerts = alertService.getAlertsByContainer(containerId);
        return ResponseEntity.ok(alerts.stream().map(mapper::toAlertDTO).toList());
    }

    @PatchMapping("/{id}/mark-sent")
    @Operation(summary = "Mark an alert as email sent")
    public ResponseEntity<AlertDTO> markAlertAsSent(@PathVariable UUID id) {
        try {
            Alert alert = alertService.markAlertAsSent(id);
            return ResponseEntity.ok(mapper.toAlertDTO(alert));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an alert")
    public ResponseEntity<Void> deleteAlert(@PathVariable UUID id) {
        try {
            alertService.deleteAlert(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
