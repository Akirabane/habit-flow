package com.akirabane.backend.unit.controller;

import com.akirabane.backend.config.SecurityTestConfig;
import com.akirabane.backend.controller.HabitController;
import com.akirabane.backend.dto.HabitRequestDto;
import com.akirabane.backend.model.HabitModel;
import com.akirabane.backend.model.UserModel;
import com.akirabane.backend.model.UserRoleModel;
import com.akirabane.backend.service.HabitService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HabitController.class)
@Import(SecurityTestConfig.class)
class HabitControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    HabitService habitService;

    @Test
    void createHabit_should_return_created_habit() throws Exception {
        UserModel user = new UserModel();
        user.setId(1L);
        user.setRole(UserRoleModel.USER);

        HabitModel habit = new HabitModel();
        habit.setId(10L);
        habit.setName("Boire de l'eau");
        habit.setUser(user);

        when(habitService.createForUser(any(Long.class), any(HabitRequestDto.class)))
                .thenReturn(habit);

        HabitRequestDto dto = new HabitRequestDto();
        dto.setName("Boire de l'eau");
        dto.setCategory("Santé");
        dto.setFrequencyType("DAILY");

        mockMvc.perform(post("/api/habits/user/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Boire de l'eau"));
    }

    @Test
    void getHabitsForUser_should_return_list() throws Exception {
        HabitModel habit = new HabitModel();
        habit.setId(10L);
        habit.setName("Boire de l'eau");

        when(habitService.getAllForUser(1L)).thenReturn(List.of(habit));

        mockMvc.perform(get("/api/habits/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10L));
    }

    @Test
    void deleteHabit_should_return_no_content() throws Exception {
        mockMvc.perform(delete("/api/habits/10"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getHabitsForUser_should_return_forbidden_when_service_throws() throws Exception {
        when(habitService.getAllForUser(1L))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN));

        mockMvc.perform(get("/api/habits/user/1"))
                .andExpect(status().isForbidden());
    }
}
