package org.home.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.home.controller.api.StatisticsController;
import org.home.dto.ErrorResponseDTO;
import org.home.dto.StatisticsInputDTO;
import org.home.service.StatisticsService;

import java.time.LocalDate;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@WebMvcTest(controllers = StatisticsController.class)
@DisplayName("StatisticsController test")
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Statistics retrieved successfully")
    void testGetStatisticsSuccess() throws Exception {
        String email = "user@example.com";
        StatisticsInputDTO inputDTO = new StatisticsInputDTO(email, "Exercise",
                LocalDate.of(2024, 10, 10),
                LocalDate.of(2024, 10, 15));

        Map<String, Map<String, String>> mockResponse = Map.of(
                "Progress Report for Habit: Exercise",
                Map.of("Success Rate", "80%", "Current Streak", "5 days")
        );

        when(statisticsService.generateProgressReport(any(StatisticsInputDTO.class))).thenReturn(mockResponse);

        String response = mockMvc.perform(get("/api/statistics/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, Map<String, String>> statResponse = objectMapper.readValue(response,
                new TypeReference<Map<String, Map<String, String>>>() { });
        assertThat(statResponse).isEqualTo(mockResponse);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Statistics not found")
    void testGetStatisticsNotFound() throws Exception {
        String email = "user@example.com";
        StatisticsInputDTO inputDTO = new StatisticsInputDTO(email, "NonExistentHabit",
                LocalDate.of(2024, 11, 1),
                LocalDate.of(2024, 11, 5));


        Map<String, Map<String, String>> mockErrorResponse = Map.of(
                "Progress Report for Habit: NonExistentHabit",
                Map.of("Error: ", "Habit not found")
        );

        when(statisticsService.generateProgressReport(any(StatisticsInputDTO.class))).thenReturn(mockErrorResponse);

        String response = mockMvc.perform(get("/api/statistics/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponseDTO errorResponse = objectMapper.readValue(response, ErrorResponseDTO.class);
        assertThat(errorResponse.getError()).isEqualTo("Habit not found");
    }
}
