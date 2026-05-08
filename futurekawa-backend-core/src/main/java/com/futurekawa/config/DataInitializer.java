package com.futurekawa.config;

import com.futurekawa.entity.User;
import com.futurekawa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Créer un utilisateur admin par défaut si n'existe pas
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                .username("admin")
                .email("admin@futurekawa.com")
                .password(passwordEncoder.encode("admin123"))
                .firstName("Admin")
                .lastName("FutureKawa")
                .role(User.UserRole.ADMIN)
                .enabled(true)
                .build();

            userRepository.save(admin);
            System.out.println("✅ Admin user created: admin / admin123");
        }

        // Créer un utilisateur opérateur par défaut si n'existe pas
        if (!userRepository.existsByUsername("operator")) {
            User operator = User.builder()
                .username("operator")
                .email("operator@futurekawa.com")
                .password(passwordEncoder.encode("operator123"))
                .firstName("Operator")
                .lastName("FutureKawa")
                .role(User.UserRole.OPERATOR)
                .enabled(true)
                .build();

            userRepository.save(operator);
            System.out.println("✅ Operator user created: operator / operator123");
        }

        // Créer un utilisateur viewer par défaut si n'existe pas
        if (!userRepository.existsByUsername("viewer")) {
            User viewer = User.builder()
                .username("viewer")
                .email("viewer@futurekawa.com")
                .password(passwordEncoder.encode("viewer123"))
                .firstName("Viewer")
                .lastName("FutureKawa")
                .role(User.UserRole.VIEWER)
                .enabled(true)
                .build();

            userRepository.save(viewer);
            System.out.println("✅ Viewer user created: viewer / viewer123");
        }
    }
}
