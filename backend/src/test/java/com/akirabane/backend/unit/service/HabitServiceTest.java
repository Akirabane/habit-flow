package com.akirabane.backend.unit.service;

import com.akirabane.backend.dto.HabitRequestDto;
import com.akirabane.backend.model.HabitModel;
import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.model.UserRoleModel;
import com.akirabane.backend.repository.HabitRepository;
import com.akirabane.backend.repository.UserRepository;
import com.akirabane.backend.service.HabitService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitServiceTest {

    @Mock
    HabitRepository habitRepository;

    @Mock
    UserRepository userRepository;

    @InjectMocks
    HabitService habitService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private void authenticate(UserModel user) {
        var auth = new UsernamePasswordAuthenticationToken(user, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void createForUser_should_create_habit_for_owner() {
        // GIVEN
        UserModel owner = new UserModel();
        owner.setId(1L);
        owner.setFullName("Joshua");
        owner.setRole(UserRoleModel.USER);

        authenticate(owner);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        HabitRequestDto dto = new HabitRequestDto();
        dto.setName("Boire de l'eau");
        dto.setCategory("Santé");
        dto.setFrequencyType("DAILY"); // selon ton type réel

        when(habitRepository.save(any(HabitModel.class))).thenAnswer(invocation -> {
            HabitModel h = invocation.getArgument(0);
            h.setId(10L);
            return h;
        });

        // WHEN
        HabitModel result = habitService.createForUser(1L, dto);

        // THEN
        ArgumentCaptor<HabitModel> captor = ArgumentCaptor.forClass(HabitModel.class);
        verify(habitRepository).save(captor.capture());

        HabitModel saved = captor.getValue();
        assertThat(saved.getName()).isEqualTo("Boire de l'eau");
        assertThat(saved.getCategory()).isEqualTo("Santé");
        assertThat(saved.getUser()).isEqualTo(owner);
        assertThat(saved.isArchived()).isFalse();

        assertThat(result.getId()).isEqualTo(10L);
    }

    @Test
    void createForUser_should_forbid_when_other_non_admin_user() {
        // GIVEN
        UserModel owner = new UserModel();
        owner.setId(1L);
        owner.setRole(UserRoleModel.USER);

        UserModel other = new UserModel();
        other.setId(2L);
        other.setRole(UserRoleModel.USER);

        authenticate(other);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        HabitRequestDto dto = new HabitRequestDto();
        dto.setName("Test");

        // WHEN / THEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> habitService.createForUser(1L, dto)
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(habitRepository, never()).save(any());
    }

    @Test
    void getAllForUser_should_return_habits_for_owner() {
        // GIVEN
        UserModel owner = new UserModel();
        owner.setId(1L);
        owner.setRole(UserRoleModel.USER);
        authenticate(owner);

        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        HabitModel h1 = new HabitModel();
        h1.setId(10L);
        h1.setUser(owner);

        when(habitRepository.findAllByUser(owner)).thenReturn(List.of(h1));

        // WHEN
        List<HabitModel> habits = habitService.getAllForUser(1L);

        // THEN
        assertThat(habits).hasSize(1);
        assertThat(habits.get(0).getId()).isEqualTo(10L);
    }

    @Test
    void getById_should_forbid_access_to_other_users_habit() {
        // GIVEN
        UserModel owner = new UserModel();
        owner.setId(1L);
        owner.setRole(UserRoleModel.USER);

        HabitModel habit = new HabitModel();
        habit.setId(10L);
        habit.setUser(owner);

        when(habitRepository.findById(10L)).thenReturn(Optional.of(habit));

        UserModel other = new UserModel();
        other.setId(2L);
        other.setRole(UserRoleModel.USER);

        authenticate(other);

        // WHEN / THEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> habitService.getById(10L)
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void delete_should_delete_when_owner() {
        // GIVEN
        UserModel owner = new UserModel();
        owner.setId(1L);
        owner.setRole(UserRoleModel.USER);
        authenticate(owner);

        HabitModel habit = new HabitModel();
        habit.setId(10L);
        habit.setUser(owner);

        when(habitRepository.findById(10L)).thenReturn(Optional.of(habit));

        // WHEN
        habitService.delete(10L);

        // THEN
        verify(habitRepository).delete(habit);
    }
}