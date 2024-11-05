package org.home.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.home.dto.HabitDTO;
import org.home.controller.api.HabitController;
import org.home.service.HabitService;
import static org.home.model.Frequency.DAILY;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@DisplayName("HabitController test")
public class HabitControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private HabitService habitService;

    @InjectMocks
    private HabitController habitController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(habitController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("All habits retrieved successfully")
    void testGetAllHabitsIsSuccess() throws Exception {
        String email = "user@example.com";
        Map<String, HabitDTO> habits = Map.of(
                "Running", new HabitDTO("Running", "Daily morning run", DAILY),
                "Reading", new HabitDTO("Reading", "Read 30 pages", DAILY));

        when(habitService.getAllHabits(email)).thenReturn(habits);

        mockMvc.perform(get("/api/habits/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.Running.title").value("Running"))
                .andExpect(jsonPath("$.Reading.title").value("Reading"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Habit created successfully")
    void testCreateHabitIsSuccess() throws Exception {
        String email = "user@example.com";
        HabitDTO habitDTO = new HabitDTO("Exercise", "Morning exercise", DAILY);
        when(habitService.createHabit(any(), any())).thenReturn(habitDTO);

        mockMvc.perform(post("/api/habits/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Exercise"))
                .andExpect(jsonPath("$.description").value("Morning exercise"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Title already exists, habit creation failed")
    void testCreateHabitTitleConflict() throws Exception {
        String email = "user@example.com";
        HabitDTO habitDTO = new HabitDTO("Exercise", "Morning exercise", DAILY);
        when(habitService.createHabit(email, habitDTO)).thenReturn(null);

        mockMvc.perform(post("/api/habits/{email}", email)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Habit with this title already exists"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Habit edited successfully")
    void testEditHabitIsSuccess() throws Exception {
        String email = "user@example.com";
        String oldTitle = "Exercise";
        HabitDTO habitDTOToUpdate = new HabitDTO("Exercise", "Evening exercise", DAILY);
        when(habitService.editHabit(any(), any(), any())).thenReturn(habitDTOToUpdate);

        mockMvc.perform(put("/api/habits/{email}/{title}", email, oldTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitDTOToUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Exercise"))
                .andExpect(jsonPath("$.description").value("Evening exercise"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Title already exists, habit editing failed")
    void testEditHabitTitleConflict() throws Exception {
        String email = "user@example.com";
        String oldTitle = "Exercise";
        HabitDTO habitDTOToUpdate = new HabitDTO("Exercise", "Evening exercise", DAILY);

        when(habitService.editHabit(any(), any(), any())).thenReturn(null);

        mockMvc.perform(put("/api/habits/{email}/{title}", email, oldTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(habitDTOToUpdate)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Habit with this title already exists"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Habit deleted successfully")
    void testDeleteHabitIsSuccess() throws Exception {
        String email = "user@example.com";
        String title = "Exercise";

        when(habitService.deleteHabit(email, title)).thenReturn(true);

        mockMvc.perform(delete("/api/habits/{email}/{title}", email, title)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Title not found, habit deletion failed")
    void testDeleteHabitNotFound() throws Exception {
        String email = "user@example.com";
        String title = "Exercise";

        when(habitService.deleteHabit(email, title)).thenReturn(false);

        mockMvc.perform(delete("/api/habits/{email}/{title}", email, title)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Habit not found"));
    }
}
