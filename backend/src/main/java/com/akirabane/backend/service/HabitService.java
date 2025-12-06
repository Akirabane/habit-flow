package com.akirabane.backend.service;

import com.akirabane.backend.dto.HabitRequest;
import com.akirabane.backend.model.Habit;
import com.akirabane.backend.repository.HabitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HabitService {

    private final HabitRepository habitRepository;

    public HabitService(HabitRepository habitRepository) {
        this.habitRepository = habitRepository;
    }

    public List<Habit> getAll() {
        return habitRepository.findAll();
    }

    public Habit getById(Long id) {
        return habitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit not found"));
    }

    public Habit create(HabitRequest request) {
        Habit habit = new Habit();
        habit.setName(request.getName());
        habit.setCategory(request.getCategory());
        habit.setFrequencyType(request.getFrequencyType());
        habit.setArchived(false);
        return habitRepository.save(habit);
    }

    public Habit update(Long id, HabitRequest request) {
        Habit existing = getById(id);
        existing.setName(request.getName());
        existing.setCategory(request.getCategory());
        existing.setFrequencyType(request.getFrequencyType());
        existing.setArchived(request.isArchived());
        return habitRepository.save(existing);
    }

    public void delete(Long id) {
        if (!habitRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Habit not found");
        }
        habitRepository.deleteById(id);
    }
}
