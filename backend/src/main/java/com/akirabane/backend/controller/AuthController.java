package com.akirabane.backend.controller;

import com.akirabane.backend.dto.AuthResponseDto;
import com.akirabane.backend.dto.LoginRequestDto;
import com.akirabane.backend.dto.RegisterRequestDto;
import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserModel register(@Valid @RequestBody RegisterRequestDto request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public AuthResponseDto login(@Valid @RequestBody LoginRequestDto request) {
        return authService.login(request);
    }

}
