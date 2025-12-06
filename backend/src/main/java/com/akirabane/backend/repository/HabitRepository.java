package com.akirabane.backend.repository;

import com.akirabane.backend.dto.HabitSummaryDto;
import com.akirabane.backend.model.HabitModel;
import com.akirabane.backend.model.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HabitRepository extends JpaRepository<HabitModel, Long> {

    List<HabitModel> findAllByUser(UserModel user);
}