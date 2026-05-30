package com.futurekawa.service;

import com.futurekawa.config.FuturekawaProperties;
import com.futurekawa.entity.Configuration;
import com.futurekawa.entity.ConfigurationAudit;
import com.futurekawa.entity.Country;
import com.futurekawa.entity.User;
import com.futurekawa.repository.ConfigurationAuditRepository;
import com.futurekawa.repository.ConfigurationRepository;
import com.futurekawa.repository.CountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ConfigurationService {

    private final ConfigurationRepository configRepository;
    private final ConfigurationAuditRepository auditRepository;
    private final CountryRepository countryRepository;
    private final FuturekawaProperties defaultProperties;

    @Cacheable(value = "configurations", key = "#countryCode")
    public Configuration getConfiguration(String countryCode) {
        Country country = countryRepository.findByCodeIso(countryCode)
            .orElseThrow(() -> new IllegalArgumentException("Country not found: " + countryCode));

        return configRepository.findByCountryId(country.getId())
            .orElseGet(() -> buildDefaultConfiguration(country));
    }

    @Cacheable(value = "configurations", key = "#countryId")
    public Configuration getConfigurationByCountryId(UUID countryId) {
        Country country = countryRepository.findById(countryId)
            .orElseThrow(() -> new IllegalArgumentException("Country not found: " + countryId));

        return configRepository.findByCountryId(countryId)
            .orElseGet(() -> buildDefaultConfiguration(country));
    }

    @CacheEvict(value = "configurations", key = "#countryCode")
    public Configuration updateConfiguration(String countryCode, Configuration newConfig, User updatingUser) {
        Country country = countryRepository.findByCodeIso(countryCode)
            .orElseThrow(() -> new IllegalArgumentException("Country not found: " + countryCode));

        Configuration current = configRepository.findByCountryId(country.getId())
            .orElseGet(() -> buildDefaultConfiguration(country));

        // Audit changed fields
        auditIfChanged(current, updatingUser, "temperatureIdeal",
            String.valueOf(current.getTemperatureIdeal()), String.valueOf(newConfig.getTemperatureIdeal()));
        auditIfChanged(current, updatingUser, "humidityIdeal",
            String.valueOf(current.getHumidityIdeal()), String.valueOf(newConfig.getHumidityIdeal()));
        auditIfChanged(current, updatingUser, "temperatureTolerance",
            String.valueOf(current.getTemperatureTolerance()), String.valueOf(newConfig.getTemperatureTolerance()));
        auditIfChanged(current, updatingUser, "humidityTolerance",
            String.valueOf(current.getHumidityTolerance()), String.valueOf(newConfig.getHumidityTolerance()));

        // Update current
        current.setTemperatureIdeal(newConfig.getTemperatureIdeal());
        current.setHumidityIdeal(newConfig.getHumidityIdeal());
        current.setTemperatureTolerance(newConfig.getTemperatureTolerance());
        current.setHumidityTolerance(newConfig.getHumidityTolerance());
        current.setTemperatureUnit(newConfig.getTemperatureUnit());

        return configRepository.save(current);
    }

    private void auditIfChanged(Configuration current, User user, String fieldName, String oldValue, String newValue) {
        if (oldValue != null && !oldValue.equals(newValue)) {
            ConfigurationAudit audit = ConfigurationAudit.builder()
                .configuration(current)
                .user(user)
                .fieldName(fieldName)
                .oldValue(oldValue)
                .newValue(newValue)
                .changedAt(LocalDateTime.now())
                .build();
            auditRepository.save(audit);
        }
    }

    public Configuration createConfiguration(Configuration config) {
        return configRepository.save(config);
    }

    @Transactional(readOnly = true)
    public List<ConfigurationAudit> getAuditHistory(UUID configurationId) {
        return auditRepository.findByConfigurationIdOrderByChangedAtDesc(configurationId);
    }

    private Configuration buildDefaultConfiguration(Country country) {
        return Configuration.builder()
            .country(country)
            .temperatureIdeal(defaultProperties.getTemperatureIdeal())
            .humidityIdeal(defaultProperties.getHumidityIdeal())
            .temperatureTolerance(defaultProperties.getTemperatureTolerance())
            .humidityTolerance(defaultProperties.getHumidityTolerance())
            .temperatureUnit("CELSIUS")
            .createdAt(LocalDateTime.now())
            .build();
    }
}
