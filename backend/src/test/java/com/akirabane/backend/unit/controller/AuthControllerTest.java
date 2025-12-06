package com.akirabane.backend.unit.controller;

import com.akirabane.backend.config.SecurityTestConfig;
import com.akirabane.backend.controller.AuthController;
import com.akirabane.backend.dto.AuthResponseDto;
import com.akirabane.backend.dto.LoginRequestDto;
import com.akirabane.backend.dto.RegisterRequestDto;
import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.model.UserRoleModel;
import com.akirabane.backend.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(SecurityTestConfig.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;

    @Test
    void register_should_return_created_user() throws Exception {
        UserModel user = new UserModel();
        user.setId(1L);
        user.setFullName("Joshua");
        user.setEmail("joshua@example.com");
        user.setRole(UserRoleModel.USER);

        when(authService.register(any(RegisterRequestDto.class))).thenReturn(user);

        RegisterRequestDto dto = new RegisterRequestDto();
        dto.setFullName("Joshua");
        dto.setEmail("joshua@example.com");
        dto.setPassword("secret123");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.fullName").value("Joshua"))
                .andExpect(jsonPath("$.email").value("joshua@example.com"));
    }

    @Test
    void login_should_return_token() throws Exception {
        AuthResponseDto response = new AuthResponseDto();
        response.setToken("fake-token");
        response.setUserId(1L);
        response.setFullName("Joshua");
        response.setEmail("joshua@example.com");
        response.setRole("USER");

        when(authService.login(any(LoginRequestDto.class))).thenReturn(response);

        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("joshua@example.com");
        dto.setPassword("secret123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-token"))
                .andExpect(jsonPath("$.userId").value(1L));
    }

    @Test
    void login_should_return_unauthorized_on_invalid_credentials() throws Exception {
        when(authService.login(any(LoginRequestDto.class)))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.UNAUTHORIZED));

        LoginRequestDto dto = new LoginRequestDto();
        dto.setEmail("joshua@example.com");
        dto.setPassword("wrong");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }
}
