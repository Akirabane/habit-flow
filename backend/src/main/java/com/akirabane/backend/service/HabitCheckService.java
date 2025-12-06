package com.akirabane.backend.service;

import com.akirabane.backend.dto.HabitStatsResponse;
import com.akirabane.backend.model.Habit;
import com.akirabane.backend.model.HabitCheck;
import com.akirabane.backend.repository.HabitCheckRepository;
import com.akirabane.backend.repository.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    public HabitStatsResponse getStats(Long habitId, LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end date must be after start date");
        }

        Habit habit = getHabitOrThrow(habitId);

        // Récupérer tous les checks sur la période
        List<HabitCheck> checks = habitCheckRepository.findAllByHabitAndDateBetween(habit, start, end);

        HabitStatsResponse stats = new HabitStatsResponse(habitId, start, end);

        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;
        stats.setTotalDays(totalDays);

        Set<LocalDate> checkedDates = checks.stream()
                .map(HabitCheck::getDate)
                .collect(Collectors.toSet());

        long checkedDays = checkedDates.size();
        stats.setCheckedDays(checkedDays);

        double successRate = totalDays > 0 ? (double) checkedDays / (double) totalDays : 0.0;
        stats.setSuccessRate(successRate);

        // Calcul longest streak
        int longestStreak = 0;
        int currentStreakIter = 0;

        for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
            if (checkedDates.contains(d)) {
                currentStreakIter++;
                if (currentStreakIter > longestStreak) {
                    longestStreak = currentStreakIter;
                }
            } else {
                currentStreakIter = 0;
            }
        }
        stats.setLongestStreak(longestStreak);

        // Calcul current streak (depuis la fin)
        int currentStreak = 0;
        for (LocalDate d = end; !d.isBefore(start); d = d.minusDays(1)) {
            if (checkedDates.contains(d)) {
                currentStreak++;
            } else {
                break;
            }
        }
        stats.setCurrentStreak(currentStreak);

        return stats;
    }
}
