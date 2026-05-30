package com.futurekawa.controller;

import com.futurekawa.dto.ContainerDTO;
import com.futurekawa.entity.Container;
import com.futurekawa.mapper.EntityMapper;
import com.futurekawa.service.ContainerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/containers")
@RequiredArgsConstructor
@Tag(name = "Containers", description = "Container management endpoints")
public class ContainerController {

    private final ContainerService containerService;
    private final EntityMapper mapper;

    @PostMapping
    @Operation(summary = "Create a new container")
    public ResponseEntity<ContainerDTO> createContainer(@Valid @RequestBody Container container) {
        Container created = containerService.createContainer(container);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toContainerDTO(created));
    }

    @GetMapping
    @Operation(summary = "List all containers")
    public ResponseEntity<List<ContainerDTO>> getAllContainers() {
        List<Container> containers = containerService.getAllContainers();
        return ResponseEntity.ok(containers.stream().map(mapper::toContainerDTO).collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a container by ID")
    public ResponseEntity<ContainerDTO> getContainerById(@PathVariable UUID id) {
        try {
            Container container = containerService.getContainerById(id);
            return ResponseEntity.ok(mapper.toContainerDTO(container));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update container")
    public ResponseEntity<ContainerDTO> updateContainer(
            @PathVariable UUID id,
            @Valid @RequestBody Container updatedContainer) {
        try {
            Container container = containerService.updateContainer(id, updatedContainer);
            return ResponseEntity.ok(mapper.toContainerDTO(container));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a container")
    public ResponseEntity<Void> deleteContainer(@PathVariable UUID id) {
        try {
            containerService.deleteContainer(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
