package com.akirabane.backend.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class HabitflowIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void full_flow_register_login_create_habit_toggle_and_get_stats() throws Exception {
        // 1) REGISTER
        String registerJsonBody = """
                {
                  "fullName": "Joshua",
                  "email": "joshua@example.com",
                  "password": "secret123"
                }
                """;

        var registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJsonBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.email").value("joshua@example.com"))
                .andReturn();

        String registerResponse = registerResult.getResponse().getContentAsString();
        JsonNode registerNode = objectMapper.readTree(registerResponse);
        long userId = registerNode.get("id").asLong();

        // 2) LOGIN
        String loginJsonBody = """
                {
                  "email": "joshua@example.com",
                  "password": "secret123"
                }
                """;

        var loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJsonBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.userId").value(userId))
                .andReturn();

        String loginResponse = loginResult.getResponse().getContentAsString();
        JsonNode loginNode = objectMapper.readTree(loginResponse);
        String token = loginNode.get("token").asText();

        String authHeader = "Bearer " + token;

        // 3) CREATE HABIT
        String habitBody = """
                {
                  "name": "Boire de l'eau",
                  "category": "Santé",
                  "frequencyType": "DAILY"
                }
                """;

        var habitResult = mockMvc.perform(post("/api/habits/user/" + userId)
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(habitBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Boire de l'eau"))
                .andReturn();

        String habitResponse = habitResult.getResponse().getContentAsString();
        JsonNode habitNode = objectMapper.readTree(habitResponse);
        long habitId = habitNode.get("id").asLong();

        // 4) TOGGLE CHECK POUR UNE DATE
        String checkBody = """
                {
                  "date": "2025-12-01"
                }
                """;

        mockMvc.perform(post("/api/habits/" + habitId + "/checks")
                        .header("Authorization", authHeader)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(checkBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 5) GET STATS SUR LA PÉRIODE
        var statsResult = mockMvc.perform(get("/api/habits/" + habitId + "/stats")
                        .header("Authorization", authHeader)
                        .param("start", "2025-12-01")
                        .param("end", "2025-12-10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDays").value(10))
                .andExpect(jsonPath("$.checkedDays").value(1))
                .andReturn();

        String statsResponse = statsResult.getResponse().getContentAsString();
        JsonNode statsNode = objectMapper.readTree(statsResponse);

        int totalDays = statsNode.get("totalDays").asInt();
        int checkedDays = statsNode.get("checkedDays").asInt();

        assertThat(totalDays).isEqualTo(10);
        assertThat(checkedDays).isEqualTo(1);
    }
}
