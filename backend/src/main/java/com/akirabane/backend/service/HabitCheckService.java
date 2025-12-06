package com.akirabane.backend.service;

import com.akirabane.backend.model.Habit;
import com.akirabane.backend.model.HabitCheck;
import com.akirabane.backend.repository.HabitCheckRepository;
import com.akirabane.backend.repository.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class HabitCheckService {

    private final HabitRepository habitRepository;
    private final HabitCheckRepository habitCheckRepository;

    public HabitCheckService(HabitRepository habitRepository,
                             HabitCheckRepository habitCheckRepository) {
        this.habitRepository = habitRepository;
        this.habitCheckRepository = habitCheckRepository;
    }

    private Habit getHabitOrThrow(Long habitId) {
        return habitRepository.findById(habitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit not found"));
    }

    public HabitCheck toggleCheck(Long habitId, LocalDate date) {
        Habit habit = getHabitOrThrow(habitId);

        return (HabitCheck) habitCheckRepository.findByHabitAndDate(habit, date)
                .map(existing -> {   // déjà coché -> on supprime (toggle off)
                    habitCheckRepository.delete(existing);
                    return null;
                })
                .orElseGet(() -> {   // pas encore coché -> on crée
                    HabitCheck created = new HabitCheck(habit, date);
                    return habitCheckRepository.save(created);
                });
    }

    public List<HabitCheck> getChecksInRange(Long habitId, LocalDate start, LocalDate end) {
        Habit habit = getHabitOrThrow(habitId);
        return habitCheckRepository.findAllByHabitAndDateBetween(habit, start, end);
    }
}
