package com.akirabane.backend.service;

import com.akirabane.backend.dto.HabitRequestDto;
import com.akirabane.backend.model.HabitModel;
import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.repository.HabitRepository;
import com.akirabane.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public HabitService(HabitRepository habitRepository,
                        UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    public List<HabitModel> getAll() {
        return habitRepository.findAll();
    }

    public List<HabitModel> getAllForUser(Long userId) {
        UserModel user = getUserOrThrow(userId);
        return habitRepository.findAllByUser(user);
    }

    public HabitModel getById(Long id) {
        return habitRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "HabitModel not found"));
    }

    private UserModel getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public HabitModel create(HabitRequestDto request) {
        HabitModel habitModel = new HabitModel();
        habitModel.setName(request.getName());
        habitModel.setCategory(request.getCategory());
        habitModel.setFrequencyType(request.getFrequencyType());
        habitModel.setArchived(false);
        return habitRepository.save(habitModel);
    }

    public HabitModel createForUser(Long userId, HabitRequestDto dto) {
        UserModel user = getUserOrThrow(userId);

        HabitModel habit = new HabitModel();
        habit.setName(dto.getName());
        habit.setCategory(dto.getCategory());
        habit.setFrequencyType(dto.getFrequencyType());
        habit.setArchived(false);
        habit.setUser(user);

        return habitRepository.save(habit);
    }

    public HabitModel update(Long id, HabitRequestDto request) {
        HabitModel existing = getById(id);
        existing.setName(request.getName());
        existing.setCategory(request.getCategory());
        existing.setFrequencyType(request.getFrequencyType());
        existing.setArchived(request.isArchived());
        return habitRepository.save(existing);
    }

    public void delete(Long id) {
        if (!habitRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "HabitModel not found");
        }
        habitRepository.deleteById(id);
    }

}
