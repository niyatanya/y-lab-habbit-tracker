package org.home.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.home.controller.api.StatisticsController;
import org.home.dto.StatisticsInputDTO;
import org.home.service.StatisticsService;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@DisplayName("StatisticsController test")
public class StatisticsControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private StatisticsController statisticsController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(statisticsController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Statistics retrieved successfully")
    void testGetStatisticsSuccess() throws Exception {
        String email = "user@example.com";
        StatisticsInputDTO inputDTO = new StatisticsInputDTO();
        inputDTO.setHabitTitle("Exercise");

        Map<String, Map<String, String>> mockResponse = Map.of(
                "Progress Report for Habit: Exercise",
                Map.of("Success Rate", "80%", "Current Streak", "5 days")
        );

        when(statisticsService.generateProgressReport(any(StatisticsInputDTO.class))).thenReturn(mockResponse);

        mockMvc.perform(get("/api/statistics/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.['Progress Report for Habit: Exercise'].['Success Rate']")
                        .value("80%"))
                .andExpect(jsonPath("$.['Progress Report for Habit: Exercise'].['Current Streak']")
                        .value("5 days"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Statistics not found")
    void testGetStatisticsNotFound() throws Exception {
        String email = "user@example.com";
        StatisticsInputDTO inputDTO = new StatisticsInputDTO();
        inputDTO.setHabitTitle("NonExistentHabit");

        Map<String, Map<String, String>> mockErrorResponse = Map.of(
                "Progress Report for Habit: NonExistentHabit",
                Map.of("Error: ", "Habit not found")
        );

        when(statisticsService.generateProgressReport(any(StatisticsInputDTO.class))).thenReturn(mockErrorResponse);

        mockMvc.perform(get("/api/statistics/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.['Progress Report for Habit: NonExistentHabit'].['Error: ']")
                        .value("Habit not found"));
    }
}
