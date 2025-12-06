package com.akirabane.backend.service;

import com.akirabane.backend.dto.AuthResponseDto;
import com.akirabane.backend.dto.LoginRequestDto;
import com.akirabane.backend.dto.RegisterRequestDto;
import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDto login(LoginRequestDto request) {
        UserModel user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return new AuthResponseDto(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }

    public UserModel register(RegisterRequestDto request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        UserModel user = new UserModel(
                request.getFullName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        return userRepository.save(user);
    }
}
