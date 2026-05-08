package com.futurekawa.service;

import com.futurekawa.dto.AuthResponse;
import com.futurekawa.dto.LoginRequest;
import com.futurekawa.entity.User;
import com.futurekawa.repository.UserRepository;
import com.futurekawa.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UUID testUserId;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();

        testUser = User.builder()
            .id(testUserId)
            .username("testuser")
            .email("test@futurekawa.local")
            .password("$2a$12$encodedpassword")
            .firstName("Test")
            .lastName("User")
            .role(User.UserRole.VIEWER)
            .enabled(true)
            .build();
    }

    @Test
    void testCreateUser_Success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@futurekawa.local")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User result = userService.createUser("newuser", "new@futurekawa.local", "password123", User.UserRole.VIEWER);

        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getRole()).isEqualTo(User.UserRole.VIEWER);
        verify(userRepository, times(1)).existsByUsername("newuser");
        verify(userRepository, times(1)).existsByEmail("new@futurekawa.local");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testCreateUser_UsernameExists() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser("testuser", "test@futurekawa.local", "password123", User.UserRole.VIEWER))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Username already exists");
        verify(userRepository, times(1)).existsByUsername("testuser");
    }

    @Test
    void testCreateUser_EmailExists() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("test@futurekawa.local")).thenReturn(true);

        assertThatThrownBy(() -> userService.createUser("newuser", "test@futurekawa.local", "password123", User.UserRole.VIEWER))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email already exists");
        verify(userRepository, times(1)).existsByEmail("test@futurekawa.local");
    }

    @Test
    void testRegisterUser_Success() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@futurekawa.local")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$hashedpassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken("testuser", testUserId.toString(), "VIEWER")).thenReturn("jwt-token-123");

        AuthResponse result = userService.registerUser("newuser", "new@futurekawa.local", "password123", User.UserRole.VIEWER);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt-token-123");
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getRole()).isEqualTo("VIEWER");
        verify(jwtService, times(1)).generateToken(anyString(), anyString(), anyString());
    }

    @Test
    void testAuthenticate_Success() {
        LoginRequest request = LoginRequest.builder()
            .username("testuser")
            .password("password123")
            .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", testUser.getPassword())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtService.generateToken("testuser", testUserId.toString(), "VIEWER")).thenReturn("jwt-token-123");

        AuthResponse result = userService.authenticate(request);

        assertThat(result).isNotNull();
        assertThat(result.getToken()).isEqualTo("jwt-token-123");
        assertThat(result.getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("password123", testUser.getPassword());
        verify(jwtService, times(1)).generateToken(anyString(), anyString(), anyString());
    }

    @Test
    void testAuthenticate_UserNotFound() {
        LoginRequest request = LoginRequest.builder()
            .username("nonexistent")
            .password("password123")
            .build();

        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.authenticate(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User not found");
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }

    @Test
    void testAuthenticate_InvalidPassword() {
        LoginRequest request = LoginRequest.builder()
            .username("testuser")
            .password("wrongpassword")
            .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpassword", testUser.getPassword())).thenReturn(false);

        assertThatThrownBy(() -> userService.authenticate(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid credentials");
        verify(userRepository, times(1)).findByUsername("testuser");
        verify(passwordEncoder, times(1)).matches("wrongpassword", testUser.getPassword());
    }

    @Test
    void testAuthenticate_UserDisabled() {
        User disabledUser = User.builder()
            .id(testUserId)
            .username("testuser")
            .email("test@futurekawa.local")
            .password("$2a$12$encodedpassword")
            .role(User.UserRole.VIEWER)
            .enabled(false)
            .build();

        LoginRequest request = LoginRequest.builder()
            .username("testuser")
            .password("password123")
            .build();

        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(disabledUser));

        assertThatThrownBy(() -> userService.authenticate(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("User account is disabled");
    }

    @Test
    void testFindByUsername_Success() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        Optional<User> result = userService.findByUsername("testuser");

        assertThat(result).isPresent();
        assertThat(result.get().getUsername()).isEqualTo("testuser");
        verify(userRepository, times(1)).findByUsername("testuser");
    }

    @Test
    void testFindByUsername_NotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Optional<User> result = userService.findByUsername("nonexistent");

        assertThat(result).isEmpty();
        verify(userRepository, times(1)).findByUsername("nonexistent");
    }
}
