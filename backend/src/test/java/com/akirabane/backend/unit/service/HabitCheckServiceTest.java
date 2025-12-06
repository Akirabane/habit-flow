package com.akirabane.backend.unit.service;

import com.akirabane.backend.dto.HabitStatsResponseDto;
import com.akirabane.backend.model.HabitCheckModel;
import com.akirabane.backend.model.HabitModel;
import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.model.UserRoleModel;
import com.akirabane.backend.repository.HabitCheckRepository;
import com.akirabane.backend.repository.HabitRepository;
import com.akirabane.backend.service.HabitCheckService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HabitCheckServiceTest {

    @Mock
    HabitRepository habitRepository;

    @Mock
    HabitCheckRepository habitCheckRepository;

    @InjectMocks
    HabitCheckService habitCheckService;

    @AfterEach
    void clearContext() {
        SecurityContextHolder.clearContext();
    }

    private HabitModel habitOwnedBy(long ownerId) {
        UserModel owner = new UserModel();
        owner.setId(ownerId);
        owner.setRole(UserRoleModel.USER);

        HabitModel habit = new HabitModel();
        habit.setId(10L);
        habit.setUser(owner);
        return habit;
    }

    private void authenticateUser(long id) {
        UserModel user = new UserModel();
        user.setId(id);
        user.setRole(UserRoleModel.USER);
        var auth = new UsernamePasswordAuthenticationToken(user, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void toggleCheck_should_create_check_when_not_existing_and_owner() {
        // GIVEN
        authenticateUser(1L);

        HabitModel habit = habitOwnedBy(1L);
        LocalDate date = LocalDate.of(2025, 12, 1);

        when(habitRepository.findById(10L)).thenReturn(Optional.of(habit));
        when(habitCheckRepository.findByHabitAndDate(habit, date))
                .thenReturn(Optional.empty());

        when(habitCheckRepository.save(any(HabitCheckModel.class))).thenAnswer(invocation -> {
            HabitCheckModel c = invocation.getArgument(0);
            c.setId(100L);
            return c;
        });

        // WHEN
        HabitCheckModel result = habitCheckService.toggleCheck(10L, date);

        // THEN
        assertThat(result.getId()).isEqualTo(100L);
        assertThat(result.getDate()).isEqualTo(date);
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getHabit()).isEqualTo(habit);
    }

    @Test
    void toggleCheck_should_forbid_when_not_owner() {
        // GIVEN
        authenticateUser(2L);

        HabitModel habit = habitOwnedBy(1L);
        LocalDate date = LocalDate.of(2025, 12, 1);

        when(habitRepository.findById(10L)).thenReturn(Optional.of(habit));

        // WHEN / THEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> habitCheckService.toggleCheck(10L, date)
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        verify(habitCheckRepository, never()).save(any());
    }

    @Test
    void getChecksInRange_should_throw_when_end_before_start() {
        // GIVEN
        LocalDate start = LocalDate.of(2025, 12, 10);
        LocalDate end = LocalDate.of(2025, 12, 1);

        // WHEN / THEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> habitCheckService.getChecksInRange(10L, start, end)
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getStats_should_throw_when_end_before_start() {
        // GIVEN
        LocalDate start = LocalDate.of(2025, 12, 10);
        LocalDate end = LocalDate.of(2025, 12, 1);

        // WHEN / THEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> habitCheckService.getStats(10L, start, end)
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void getStats_should_forbid_when_not_owner() {
        // GIVEN
        authenticateUser(2L);
        LocalDate start = LocalDate.of(2025, 12, 1);
        LocalDate end = LocalDate.of(2025, 12, 10);

        HabitModel habit = habitOwnedBy(1L);
        when(habitRepository.findById(10L)).thenReturn(Optional.of(habit));

        // WHEN / THEN
        ResponseStatusException ex = assertThrows(
                ResponseStatusException.class,
                () -> habitCheckService.getStats(10L, start, end)
        );

        assertThat(ex.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }
}
