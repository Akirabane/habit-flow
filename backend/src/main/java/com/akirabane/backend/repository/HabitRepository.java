package com.akirabane.backend.repository;

import com.akirabane.backend.model.HabitModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HabitRepository extends JpaRepository<HabitModel, Long> {
    /**
     * En attente
     */
}