package com.akirabane.backend.controller;

import com.akirabane.backend.dto.UserWithHabitsDto;
import com.akirabane.backend.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /api/users/1
    @GetMapping("/{id}")
    public UserWithHabitsDto getUserWithHabits(@PathVariable Long id) {
        return userService.getUserWithHabits(id);
    }
}
