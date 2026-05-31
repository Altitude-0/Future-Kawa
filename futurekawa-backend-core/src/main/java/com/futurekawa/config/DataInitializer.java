package com.futurekawa.config;

import com.futurekawa.entity.User;
import com.futurekawa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DataSource dataSource;
    private final PasswordEncoder passwordEncoder;
    private final FuturekawaProperties properties;

    @Override
    public void run(String... args) throws Exception {
        // 1. Check if the database is empty by counting the number of users
        if (userRepository.count() == 0) {
            log.info("🗄️ Database is empty, initializing seed data...");
            
            // 2. Create default users programmatically (Safe from Sonar S8215)
            createDefaultUsers();

            // 3. Execute the rest of the seed data from SQL
            try {
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("db/scripts/seed_data.sql"));
                populator.execute(dataSource);
                log.info("✅ Seed data SQL executed successfully!");
            } catch (Exception e) {
                log.error("❌ Error while executing seed_data.sql: {}", e.getMessage(), e);
            }
        } else {
            log.info("ℹ️ Database already contains data, skipping initialization.");
        }
    }

    private void createDefaultUsers() {
        String encodedPassword = passwordEncoder.encode(properties.getSeedData().getUserPassword());

        User admin = User.builder()
            .id(UUID.fromString("a1234567-89ab-cdef-0123-456789abcdef"))
            .username("admin_user")
            .email("admin@futurekawa.local")
            .password(encodedPassword)
            .role(User.UserRole.ADMIN)
            .enabled(true)
            .createdAt(LocalDateTime.now())
            .build();

        User manager = User.builder()
            .id(UUID.fromString("b1234567-89ab-cdef-0123-456789abcdef"))
            .username("manager_user")
            .email("manager@futurekawa.local")
            .password(encodedPassword)
            .role(User.UserRole.OPERATOR) // Updated to OPERATOR to match UserRole enum
            .enabled(true)
            .createdAt(LocalDateTime.now())
            .build();

        User viewer = User.builder()
            .id(UUID.fromString("c1234567-89ab-cdef-0123-456789abcdef"))
            .username("viewer_user")
            .email("viewer@futurekawa.local")
            .password(encodedPassword)
            .role(User.UserRole.VIEWER)
            .enabled(true)
            .createdAt(LocalDateTime.now())
            .build();

        userRepository.save(admin);
        userRepository.save(manager);
        userRepository.save(viewer);
        log.info("👤 Default users created successfully.");
    }
}
