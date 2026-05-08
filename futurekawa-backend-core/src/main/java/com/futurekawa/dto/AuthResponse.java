package com.futurekawa.dto;

import com.futurekawa.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String username;
    private String email;
    private String role;

    public static AuthResponse fromUser(User user, String token) {
        return AuthResponse.builder()
            .token(token)
            .type("Bearer")
            .username(user.getUsername())
            .email(user.getEmail())
            .role(user.getRole().toString())
            .build();
    }
}
