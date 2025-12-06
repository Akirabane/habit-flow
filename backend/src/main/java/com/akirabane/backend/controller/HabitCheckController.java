package com.akirabane.backend.controller;

import com.akirabane.backend.dto.HabitCheckRequest;
import com.akirabane.backend.model.HabitCheck;
import com.akirabane.backend.service.HabitCheckService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/habits")
public class HabitCheckController {

    private final HabitCheckService habitCheckService;

    public HabitCheckController(HabitCheckService habitCheckService) {
        this.habitCheckService = habitCheckService;
    }

    // POST /api/habits/{id}/checks  body: { "date": "2025-12-06" }
    @PostMapping("/{habitId}/checks")
    @ResponseStatus(HttpStatus.OK)
    public HabitCheck toggleCheck(@PathVariable Long habitId,
                                  @RequestBody HabitCheckRequest request) {
        return habitCheckService.toggleCheck(habitId, request.getDate());
    }

    // GET /api/habits/{id}/checks?start=2025-12-01&end=2025-12-31
    @GetMapping("/{habitId}/checks")
    public List<HabitCheck> getChecksInRange(
            @PathVariable Long habitId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {

        return habitCheckService.getChecksInRange(habitId, start, end);
    }
}
