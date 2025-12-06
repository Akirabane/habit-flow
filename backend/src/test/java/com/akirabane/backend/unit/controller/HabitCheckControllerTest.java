package com.akirabane.backend.unit.controller;

import com.akirabane.backend.config.SecurityTestConfig;
import com.akirabane.backend.controller.HabitCheckController;
import com.akirabane.backend.dto.HabitStatsResponseDto;
import com.akirabane.backend.model.HabitCheckModel;
import com.akirabane.backend.service.HabitCheckService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(HabitCheckController.class)
@Import(SecurityTestConfig.class)
class HabitCheckControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    HabitCheckService habitCheckService;

    @Test
    void toggleCheck_should_return_check() throws Exception {
        HabitCheckModel check = new HabitCheckModel();
        check.setId(100L);
        check.setSuccess(true);
        check.setDate(LocalDate.of(2025, 12, 1));

        when(habitCheckService.toggleCheck(any(Long.class), any(LocalDate.class)))
                .thenReturn(check);

        String body = """
                {
                  "date": "2025-12-01"
                }
                """;

        mockMvc.perform(post("/api/habits/10/checks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100L))
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getChecks_should_return_list() throws Exception {
        HabitCheckModel check = new HabitCheckModel();
        check.setId(100L);
        check.setSuccess(true);
        check.setDate(LocalDate.of(2025, 12, 1));

        when(habitCheckService.getChecksInRange(10L,
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 10)))
                .thenReturn(List.of(check));

        mockMvc.perform(get("/api/habits/10/checks")
                        .param("start", "2025-12-01")
                        .param("end", "2025-12-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100L));
    }

    @Test
    void getStats_should_return_stats() throws Exception {
        HabitStatsResponseDto stats = new HabitStatsResponseDto();
        stats.setTotalDays(10);
        stats.setCheckedDays(3);

        when(habitCheckService.getStats(10L,
                LocalDate.of(2025, 12, 1),
                LocalDate.of(2025, 12, 10)))
                .thenReturn(stats);

        mockMvc.perform(get("/api/habits/10/stats")
                        .param("start", "2025-12-01")
                        .param("end", "2025-12-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDays").value(10))
                .andExpect(jsonPath("$.checkedDays").value(3));
    }

    @Test
    void getStats_should_return_forbidden_when_service_throws() throws Exception {
        when(habitCheckService.getStats(any(Long.class), any(LocalDate.class), any(LocalDate.class)))
                .thenThrow(new ResponseStatusException(org.springframework.http.HttpStatus.FORBIDDEN));

        mockMvc.perform(get("/api/habits/10/stats")
                        .param("start", "2025-12-01")
                        .param("end", "2025-12-10"))
                .andExpect(status().isForbidden());
    }
}
