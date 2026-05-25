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

    /**
     * Get configuration for a country by country code with fallback to default properties.
     * Result is cached for 5 minutes to avoid repeated DB queries.
     */
    @Cacheable(value = "configurations", key = "#countryCode")
    public Configuration getConfiguration(String countryCode) {
        Country country = countryRepository.findByCode(countryCode)
            .orElseThrow(() -> new IllegalArgumentException("Country not found: " + countryCode));

        return configRepository.findByCountryId(country.getId())
            .orElseGet(() -> buildDefaultConfiguration(country));
    }

    /**
     * Get configuration for a country by country ID with fallback to default properties.
     */
    @Cacheable(value = "configurations", key = "#countryId")
    public Configuration getConfigurationByCountryId(UUID countryId) {
        Country country = countryRepository.findById(countryId)
            .orElseThrow(() -> new IllegalArgumentException("Country not found: " + countryId));

        return configRepository.findByCountryId(countryId)
            .orElseGet(() -> buildDefaultConfiguration(country));
    }

    /**
     * Update configuration and record audit entries for each changed field.
     * @CacheEvict invalidates cache for this country code after update.
     */
    @CacheEvict(value = "configurations", key = "#countryCode")
    public Configuration updateConfiguration(String countryCode, Configuration newConfig, User updatingUser) {
        Country country = countryRepository.findByCode(countryCode)
            .orElseThrow(() -> new IllegalArgumentException("Country not found: " + countryCode));

        Configuration current = configRepository.findByCountryId(country.getId())
            .orElseGet(() -> buildDefaultConfiguration(country));

        // Compare each field and create audit entry if changed
        auditIfChanged(current, newConfig, updatingUser, "temperatureIdeal",
            String.valueOf(current.getTemperatureIdeal()),
            String.valueOf(newConfig.getTemperatureIdeal()));

        auditIfChanged(current, newConfig, updatingUser, "temperatureTolerance",
            String.valueOf(current.getTemperatureTolerance()),
            String.valueOf(newConfig.getTemperatureTolerance()));

        auditIfChanged(current, newConfig, updatingUser, "temperatureUnit",
            current.getTemperatureUnit().toString(),
            newConfig.getTemperatureUnit().toString());

        auditIfChanged(current, newConfig, updatingUser, "alertOldLotDays",
            String.valueOf(current.getAlertOldLotDays()),
            String.valueOf(newConfig.getAlertOldLotDays()));

        // Update current with new values
        current.setTemperatureIdeal(newConfig.getTemperatureIdeal());
        current.setTemperatureTolerance(newConfig.getTemperatureTolerance());
        current.setTemperatureUnit(newConfig.getTemperatureUnit());
        current.setAlertOldLotDays(newConfig.getAlertOldLotDays());
        current.setUpdatedAt(LocalDateTime.now());

        return configRepository.save(current);
    }

    /**
     * Convert temperature between Celsius and Fahrenheit.
     * @param value The temperature value to convert
     * @param from Source unit (CELSIUS or FAHRENHEIT)
     * @param to Target unit (CELSIUS or FAHRENHEIT)
     * @return Converted temperature value
     */
    public Float convertTemperature(Float value, Configuration.TemperatureUnit from, Configuration.TemperatureUnit to) {
        if (from == to) {
            return value;
        }

        if (from == Configuration.TemperatureUnit.CELSIUS && to == Configuration.TemperatureUnit.FAHRENHEIT) {
            return (value * 9.0f / 5.0f) + 32.0f;
        }

        if (from == Configuration.TemperatureUnit.FAHRENHEIT && to == Configuration.TemperatureUnit.CELSIUS) {
            return (value - 32.0f) * 5.0f / 9.0f;
        }

        return value;
    }

    // Helper methods

    /**
     * Create audit entry if field value changed.
     */
    private void auditIfChanged(Configuration current, Configuration newConfig, User user,
                                String fieldName, String oldValue, String newValue) {
        if (!oldValue.equals(newValue)) {
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

    /**
     * Create a new configuration for a country.
     * Invalidates cache for this country after creation.
     */
    @CacheEvict(value = "configurations", key = "#config.country.code")
    public Configuration createConfiguration(Configuration config) {
        return configRepository.save(config);
    }

    /**
     * Retrieve audit history for a configuration, ordered by most recent first.
     */
    @Transactional(readOnly = true)
    public List<ConfigurationAudit> getAuditHistory(UUID configurationId) {
        return auditRepository.findByConfigurationIdOrderByChangedAtDesc(configurationId);
    }

    private Configuration buildDefaultConfiguration(Country country) {
        return Configuration.builder()
            .country(country)
            .temperatureIdeal(defaultProperties.getTemperatureIdeal())
            .temperatureTolerance(defaultProperties.getTemperatureTolerance())
            .temperatureUnit(Configuration.TemperatureUnit.CELSIUS)
            .alertOldLotDays(defaultProperties.getAlertOldLotDays().intValue())
            .build();
    }
}
