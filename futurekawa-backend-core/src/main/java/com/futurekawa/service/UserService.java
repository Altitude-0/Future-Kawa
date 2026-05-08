package com.futurekawa.service;

import com.futurekawa.dto.AuthResponse;
import com.futurekawa.dto.LoginRequest;
import com.futurekawa.entity.User;
import com.futurekawa.repository.UserRepository;
import com.futurekawa.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse authenticate(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        // Mettre à jour lastLogin
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // Générer le token JWT
        String token = jwtService.generateToken(user.getUsername(), user.getId().toString(), user.getRole().toString());

        return AuthResponse.fromUser(user, token);
    }

    public AuthResponse registerUser(String username, String email, String password, User.UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .role(role)
            .enabled(true)
            .build();

        user = userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername(), user.getId().toString(), user.getRole().toString());
        return AuthResponse.fromUser(user, token);
    }

    public User createUser(String username, String email, String password, User.UserRole role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = User.builder()
            .username(username)
            .email(email)
            .password(passwordEncoder.encode(password))
            .role(role)
            .enabled(true)
            .build();

        return userRepository.save(user);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public User updateUser(UUID userId, User updates) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (updates.getFirstName() != null) {
            user.setFirstName(updates.getFirstName());
        }
        if (updates.getLastName() != null) {
            user.setLastName(updates.getLastName());
        }
        if (updates.getEmail() != null && !updates.getEmail().equals(user.getEmail())) {
            if (userRepository.existsByEmail(updates.getEmail())) {
                throw new IllegalArgumentException("Email already exists");
            }
            user.setEmail(updates.getEmail());
        }
        if (updates.getEnabled() != null) {
            user.setEnabled(updates.getEnabled());
        }

        return userRepository.save(user);
    }

    public void deleteUser(UUID userId) {
        userRepository.deleteById(userId);
    }
}
