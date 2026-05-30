package com.futurekawa.config;

import com.futurekawa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DataSource dataSource;

    @Override
    public void run(String... args) throws Exception {
        // On vérifie si la base est vide (pas d'utilisateurs)
        if (userRepository.count() == 0) {
            log.info("🗄️ Database is empty, executing seed_data.sql...");
            try {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("db/scripts/seed_data.sql"));
                populator.execute(dataSource);
                log.info("✅ Seed data executed successfully!");
            } catch (Exception e) {
                log.error("❌ Error while executing seed_data.sql: {}", e.getMessage(), e);
            }
        } else {
            log.info("ℹ️ Database already contains data, skipping seed_data.sql.");
        }
    }
}
