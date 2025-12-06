package com.akirabane.backend.service;

import com.akirabane.backend.dto.HabitStatsResponseDto;
import com.akirabane.backend.model.HabitModel;
import com.akirabane.backend.model.HabitCheckModel;
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

    private HabitModel getHabitOrThrow(Long habitId) {
        return habitRepository.findById(habitId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "HabitModel not found"));
    }

    public HabitCheckModel toggleCheck(Long habitId, LocalDate date) {
        HabitModel habitModel = getHabitOrThrow(habitId);

        return (HabitCheckModel) habitCheckRepository.findByHabitAndDate(habitModel, date)
                .map(existing -> {   // déjà coché -> on supprime (toggle off)
                    habitCheckRepository.delete(existing);
                    return null;
                })
                .orElseGet(() -> {   // pas encore coché -> on crée
                    HabitCheckModel created = new HabitCheckModel(habitModel, date);
                    return habitCheckRepository.save(created);
                });
    }

    public List<HabitCheckModel> getChecksInRange(Long habitId, LocalDate start, LocalDate end) {
        HabitModel habitModel = getHabitOrThrow(habitId);
        return habitCheckRepository.findAllByHabitAndDateBetween(habitModel, start, end);
    }

    public HabitStatsResponseDto getStats(Long habitId, LocalDate start, LocalDate end) {
        if (end.isBefore(start)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "end date must be after start date");
        }

        HabitModel habitModel = getHabitOrThrow(habitId);

        // Récupérer tous les checks sur la période
        List<HabitCheckModel> checks = habitCheckRepository.findAllByHabitAndDateBetween(habitModel, start, end);

        HabitStatsResponseDto stats = new HabitStatsResponseDto(habitId, start, end);

        long totalDays = ChronoUnit.DAYS.between(start, end) + 1;
        stats.setTotalDays(totalDays);

        Set<LocalDate> checkedDates = checks.stream()
                .map(HabitCheckModel::getDate)
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
