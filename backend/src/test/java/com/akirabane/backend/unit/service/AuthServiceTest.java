package com.akirabane.backend.unit.service;

import com.akirabane.backend.dto.AuthResponseDto;
import com.akirabane.backend.dto.LoginRequestDto;
import com.akirabane.backend.dto.RegisterRequestDto;
import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.model.UserRoleModel;
import com.akirabane.backend.repository.UserRepository;
import com.akirabane.backend.service.AuthService;
import com.akirabane.backend.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @InjectMocks
    AuthService authService;

    @Test
    void register_should_encode_password_and_save_user_with_role_user() {
        // GIVEN
        RegisterRequestDto request = new RegisterRequestDto();
        request.setFullName("Joshua");
        request.setEmail("joshua@example.com");
        request.setPassword("secret123");

        when(userRepository.findByEmail("joshua@example.com"))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode("secret123"))
                .thenReturn("encoded-secret");

        // on capture l'utilisateur passé à save(...)
        ArgumentCaptor<UserModel> userCaptor = ArgumentCaptor.forClass(UserModel.class);

        // on simule la génération d'un id par la base
        when(userRepository.save(any(UserModel.class))).thenAnswer(invocation -> {
            UserModel u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        // WHEN
        UserModel result = authService.register(request);

        // THEN
        verify(userRepository).findByEmail("joshua@example.com");
        verify(passwordEncoder).encode("secret123");
        verify(userRepository).save(userCaptor.capture());

        UserModel userToSave = userCaptor.getValue();
        assertThat(userToSave.getFullName()).isEqualTo("Joshua");
        assertThat(userToSave.getEmail()).isEqualTo("joshua@example.com");
        assertThat(userToSave.getPassword()).isEqualTo("encoded-secret");
        assertThat(userToSave.getRole()).isEqualTo(UserRoleModel.USER);

        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void register_should_throw_if_email_already_exists() {
        // GIVEN
        RegisterRequestDto request = new RegisterRequestDto();
        request.setFullName("Joshua");
        request.setEmail("joshua@example.com");
        request.setPassword("secret123");

        UserModel existing = new UserModel();
        existing.setId(1L);

        when(userRepository.findByEmail("joshua@example.com"))
                .thenReturn(Optional.of(existing));

        // WHEN / THEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authService.register(request)
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }

    @Test
    void login_should_return_token_and_user_info_when_credentials_are_valid() {
        // GIVEN
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("joshua@example.com");
        request.setPassword("secret123");

        UserModel user = new UserModel();
        user.setId(1L);
        user.setFullName("Joshua");
        user.setEmail("joshua@example.com");
        user.setPassword("encoded-secret");
        user.setRole(UserRoleModel.USER);

        when(userRepository.findByEmail("joshua@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret123", "encoded-secret"))
                .thenReturn(true);
        when(jwtService.generateToken(user))
                .thenReturn("fake-jwt-token");

        // WHEN
        AuthResponseDto response = authService.login(request);

        // THEN
        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getFullName()).isEqualTo("Joshua");
        assertThat(response.getEmail()).isEqualTo("joshua@example.com");
        assertThat(response.getRole()).isEqualTo("USER");
    }

    @Test
    void login_should_throw_if_credentials_invalid() {
        // GIVEN
        LoginRequestDto request = new LoginRequestDto();
        request.setEmail("joshua@example.com");
        request.setPassword("wrong-password");

        UserModel user = new UserModel();
        user.setId(1L);
        user.setEmail("joshua@example.com");
        user.setPassword("encoded-secret");
        user.setRole(UserRoleModel.USER);

        when(userRepository.findByEmail("joshua@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-secret"))
                .thenReturn(false);

        // WHEN / THEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> authService.login(request)
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}