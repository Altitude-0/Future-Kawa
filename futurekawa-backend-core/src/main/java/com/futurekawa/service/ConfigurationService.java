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

    private static final String ERR_COUNTRY_NOT_FOUND = "Country not found: ";
    private final ConfigurationRepository configRepository;
    private final ConfigurationAuditRepository auditRepository;
    private final CountryRepository countryRepository;
    private final FuturekawaProperties defaultProperties;

    @Cacheable(value = "configurations", key = "#countryCode")
    public Configuration getConfiguration(String countryCode) {
        Country country = countryRepository.findByCodeIso(countryCode)
            .orElseThrow(() -> new IllegalArgumentException(ERR_COUNTRY_NOT_FOUND + countryCode));

        return configRepository.findByCountryId(country.getId())
            .orElseGet(() -> buildDefaultConfiguration(country));
    }

    @Cacheable(value = "configurations", key = "#countryId")
    public Configuration getConfigurationByCountryId(UUID countryId) {
        Country country = countryRepository.findById(countryId)
            .orElseThrow(() -> new IllegalArgumentException(ERR_COUNTRY_NOT_FOUND + countryId));

        return configRepository.findByCountryId(countryId)
            .orElseGet(() -> buildDefaultConfiguration(country));
    }

    @CacheEvict(value = "configurations", key = "#countryCode")
    public Configuration updateConfiguration(String countryCode, Configuration newConfig, User updatingUser) {
        Country country = countryRepository.findByCodeIso(countryCode)
            .orElseThrow(() -> new IllegalArgumentException(ERR_COUNTRY_NOT_FOUND + countryCode));

        Configuration current = configRepository.findByCountryId(country.getId())
            .orElseGet(() -> buildDefaultConfiguration(country));

        // Audit and update fields
        auditAndUpdateFields(current, newConfig, updatingUser);

        return configRepository.save(current);
    }

    private void auditAndUpdateFields(Configuration current, Configuration newConfig, User user) {
        auditIfChanged(current, user, "temperatureIdeal", current.getTemperatureIdeal(), newConfig.getTemperatureIdeal());
        auditIfChanged(current, user, "humidityIdeal", current.getHumidityIdeal(), newConfig.getHumidityIdeal());
        auditIfChanged(current, user, "temperatureTolerance", current.getTemperatureTolerance(), newConfig.getTemperatureTolerance());
        auditIfChanged(current, user, "humidityTolerance", current.getHumidityTolerance(), newConfig.getHumidityTolerance());

        current.setTemperatureIdeal(newConfig.getTemperatureIdeal());
        current.setHumidityIdeal(newConfig.getHumidityIdeal());
        current.setTemperatureTolerance(newConfig.getTemperatureTolerance());
        current.setHumidityTolerance(newConfig.getHumidityTolerance());
        current.setTemperatureUnit(newConfig.getTemperatureUnit());
    }

    private void auditIfChanged(Configuration current, User user, String fieldName, Object oldValue, Object newValue) {
        if (oldValue != null && !oldValue.equals(newValue)) {
            ConfigurationAudit audit = ConfigurationAudit.builder()
                .configuration(current)
                .user(user)
                .fieldName(fieldName)
                .oldValue(String.valueOf(oldValue))
                .newValue(String.valueOf(newValue))
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
