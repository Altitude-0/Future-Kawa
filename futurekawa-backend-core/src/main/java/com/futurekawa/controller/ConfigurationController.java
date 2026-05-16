package com.futurekawa.controller;

import com.futurekawa.dto.ConfigurationAuditDTO;
import com.futurekawa.dto.ConfigurationDTO;
import com.futurekawa.entity.Configuration;
import com.futurekawa.entity.Country;
import com.futurekawa.entity.User;
import com.futurekawa.mapper.EntityMapper;
import com.futurekawa.repository.CountryRepository;
import com.futurekawa.service.ConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/configurations")
@RequiredArgsConstructor
@Tag(name = "Configurations", description = "Country-specific configuration management")
public class ConfigurationController {

    private final ConfigurationService configService;
    private final EntityMapper mapper;
    private final CountryRepository countryRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create new configuration for a country (ADMIN only)")
    public ResponseEntity<ConfigurationDTO> createConfiguration(
            @Valid @RequestBody ConfigurationDTO configDTO) {

        Country country = countryRepository.findByCode(configDTO.getCountryCode())
            .orElseThrow(() -> new IllegalArgumentException("Country not found: " + configDTO.getCountryCode()));

        Configuration config = mapper.toConfigurationEntity(configDTO, country);
        Configuration saved = configService.createConfiguration(config);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(mapper.toConfigurationDTO(saved));
    }

    @GetMapping("/{countryCode}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get configuration for a country (ADMIN only)")
    public ResponseEntity<ConfigurationDTO> getConfiguration(@PathVariable String countryCode) {
        Configuration config = configService.getConfiguration(countryCode);
        return ResponseEntity.ok(mapper.toConfigurationDTO(config));
    }

    @PutMapping("/{countryCode}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update configuration (ADMIN only)")
    public ResponseEntity<ConfigurationDTO> updateConfiguration(
            @PathVariable String countryCode,
            @Valid @RequestBody ConfigurationDTO newConfigDTO,
            Authentication authentication) {

        Configuration config = configService.getConfiguration(countryCode);
        Configuration newConfig = mapper.toConfigurationEntity(newConfigDTO, config.getCountry());
        User currentUser = (User) authentication.getPrincipal();

        Configuration updated = configService.updateConfiguration(countryCode, newConfig, currentUser);
        return ResponseEntity.ok(mapper.toConfigurationDTO(updated));
    }

    @GetMapping("/{countryCode}/audit")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get audit history for a configuration (ADMIN only)")
    public ResponseEntity<List<ConfigurationAuditDTO>> getAuditHistory(@PathVariable String countryCode) {
        Configuration config = configService.getConfiguration(countryCode);
        List<ConfigurationAuditDTO> audits = configService.getAuditHistory(config.getId())
            .stream()
            .map(mapper::toConfigurationAuditDTO)
            .collect(Collectors.toList());
        return ResponseEntity.ok(audits);
    }
}
