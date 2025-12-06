package com.akirabane.backend.controller;

import com.akirabane.backend.dto.HabitRequest;
import com.akirabane.backend.model.Habit;
import com.akirabane.backend.service.HabitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private final HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    // GET /api/habits -> liste de toutes les habitudes
    @GetMapping
    public List<Habit> getAllHabits() {
        return habitService.getAll();
    }

    // GET /api/habits/{id}
    @GetMapping("/{id}")
    public Habit getHabitById(@PathVariable Long id) {
        return habitService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Habit createHabit(@Valid @RequestBody HabitRequest request) {
        return habitService.create(request);
    }

    @PutMapping("/{id}")
    public Habit updateHabit(@PathVariable Long id,
                             @Valid @RequestBody HabitRequest request) {
        return habitService.update(id, request);
    }

    // DELETE /api/habits/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHabit(@PathVariable Long id) {
        habitService.delete(id);
    }
}
