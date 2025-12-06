package com.akirabane.backend.repository;

import com.akirabane.backend.model.HabitModel;
import com.akirabane.backend.model.HabitCheckModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HabitCheckRepository extends JpaRepository<HabitCheckModel, Long> {

    Optional<HabitCheckModel> findByHabitAndDate(HabitModel habitModel, LocalDate date);

    List<HabitCheckModel> findAllByHabitAndDateBetween(HabitModel habit, LocalDate start, LocalDate end);
}