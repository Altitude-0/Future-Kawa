package com.futurekawa.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "futurekawa")
public class FuturekawaProperties {

    private String country = "generic";

    private Float temperatureIdeal = 28.0f;
    private Float humidityIdeal = 60.0f;
    private Float temperatureTolerance = 3.0f;
    private Float humidityTolerance = 2.0f;
    private Long alertOldLotDays = 365L;

    private JwtProperties jwt = new JwtProperties();
    private SeedDataProperties seedData = new SeedDataProperties();

    @Data
    public static class JwtProperties {
        private String secret = "futurekawa-secret-key-for-jwt-token-generation-and-validation-2024";
        private Long expiration = 86400000L;
    }

    @Data
    public static class SeedDataProperties {
        private String userPassword = "password123";
    }
}
