package com.akirabane.backend.controller;

import com.akirabane.backend.model.Habit;
import com.akirabane.backend.repository.HabitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("api/health")
    public String health() {
        return "OK, backend running.";
    }

    @Autowired
    private HabitRepository habitRepository;

    @GetMapping("/api/test-create")
    public String testCreateHabit() {
        Habit habit = new Habit();
        habit.setName("Test Habit");
        habit.setCategory("General");
        habitRepository.save(habit);
        return "Habit created with ID: " + habit.getId();
    }
}
