package com.akirabane.backend.service;

import com.akirabane.backend.dto.HabitSummaryDto;
import com.akirabane.backend.dto.UserWithHabitsDto;
import com.akirabane.backend.model.HabitModel;
import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.repository.HabitRepository;
import com.akirabane.backend.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;

    public UserService(UserRepository userRepository,
                       HabitRepository habitRepository) {
        this.userRepository = userRepository;
        this.habitRepository = habitRepository;
    }

    private UserModel getUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    public UserWithHabitsDto getUserWithHabits(Long userId) {
        UserModel user = getUserOrThrow(userId);
        List<HabitModel> habits = habitRepository.findAllByUser(user);

        List<HabitSummaryDto> habitDtos = habits.stream()
                .map(h -> new HabitSummaryDto(
                        h.getId(),
                        h.getName(),
                        h.getCategory(),
                        h.getFrequencyType(),
                        h.isArchived()
                ))
                .toList();

        return new UserWithHabitsDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                habitDtos
        );
    }
}
