package com.akirabane.backend.unit.security;

import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.model.UserRoleModel;
import com.akirabane.backend.service.JwtService;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class JwtServiceTest {

    @Test
    void generateToken_and_validate_it() {
        // GIVEN
        String secret = "this-is-a-very-long-secret-key-for-tests-1234567890";
        long expirationMs = 3600000; // 1h

        JwtService jwtService = new JwtService(secret, expirationMs);

        UserModel user = new UserModel();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setFullName("Test User");
        user.setRole(UserRoleModel.USER);
        user.setPassword("irrelevant");

        // WHEN
        String token = jwtService.generateToken(user);

        // THEN
        assertThat(token).isNotBlank();

        String email = jwtService.extractEmail(token);
        assertThat(email).isEqualTo("test@example.com");

        boolean valid = jwtService.isTokenValid(token, "test@example.com");
        assertThat(valid).isTrue();
    }
}