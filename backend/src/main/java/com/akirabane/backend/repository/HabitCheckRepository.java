package com.akirabane.backend.repository;

import com.akirabane.backend.model.Habit;
import com.akirabane.backend.model.HabitCheck;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitCheckRepository extends JpaRepository<HabitCheck, Long> {

    Optional<HabitCheck> findByHabitAndDate(Habit habit, LocalDate date);

    List<HabitCheck> findAllByHabitAndDateBetween(Habit habit, LocalDate start, LocalDate end);
}