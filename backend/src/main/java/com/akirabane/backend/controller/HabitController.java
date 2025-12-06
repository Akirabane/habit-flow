package com.akirabane.backend.controller;

import com.akirabane.backend.dto.HabitRequestDto;
import com.akirabane.backend.model.HabitModel;
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
    public List<HabitModel> getAllHabits() {
        return habitService.getAll();
    }

    // GET /api/habits/{id}
    @GetMapping("/{id}")
    public HabitModel getHabitById(@PathVariable Long id) {
        return habitService.getById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HabitModel createHabit(@Valid @RequestBody HabitRequestDto request) {
        return habitService.create(request);
    }

    @PutMapping("/{id}")
    public HabitModel updateHabit(@PathVariable Long id,
                                  @Valid @RequestBody HabitRequestDto request) {
        return habitService.update(id, request);
    }

    // DELETE /api/habits/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteHabit(@PathVariable Long id) {
        habitService.delete(id);
    }
}
