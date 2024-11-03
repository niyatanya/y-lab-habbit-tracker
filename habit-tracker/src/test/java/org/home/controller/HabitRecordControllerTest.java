package org.home.controller;

import org.home.controller.api.HabitRecordController;
import org.home.dto.HabitRecordDTO;
import org.home.service.HabitRecordService;
import java.time.LocalDate;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@DisplayName("HabitRecordController test")
public class HabitRecordControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HabitRecordService recordService;

    @InjectMocks
    private HabitRecordController recordController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recordController).build();
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("All records of a habit retrieved successfully")
    void testGetAllRecordsIsSuccess() throws Exception {
        String email = "user@example.com";
        String habitTitle = "Exercise";
        Map<LocalDate, HabitRecordDTO> records = Map.of(
                LocalDate.of(2024, 11, 1),
                new HabitRecordDTO(LocalDate.of(2024, 11, 1), true),
                LocalDate.of(2024, 11, 2),
                new HabitRecordDTO(LocalDate.of(2024, 11, 2), false));

        when(recordService.getAllRecords(email, habitTitle)).thenReturn(records);

        mockMvc.perform(get("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andExpect(jsonPath("$.['2024-11-01'].date").value("2024-11-01"))
                .andExpect(jsonPath("$.['2024-11-01'].completed").value(true))
                .andExpect(jsonPath("$.['2024-11-02'].completed").value(false));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Record created successfully")
    void testCreateRecordIsSuccess() throws Exception {
        String email = "user@example.com";
        String habitTitle = "Exercise";
        HabitRecordDTO recordDTO = new HabitRecordDTO(LocalDate.of(2024, 11, 3), true);

        when(recordService.createRecord(eq(email), eq(habitTitle), any())).thenReturn(recordDTO);

        mockMvc.perform(post("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2024-11-03\", \"completed\":true}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.date").value("2024-11-03"))
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Date already exists, record creation failed")
    void testCreateRecordConflict() throws Exception {
        String email = "user@example.com";
        String habitTitle = "Exercise";
        HabitRecordDTO recordDTO = new HabitRecordDTO(LocalDate.of(2024, 11, 3), true);

        when(recordService.createRecord(eq(email), eq(habitTitle), any())).thenReturn(null);

        mockMvc.perform(post("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2024-11-03\", \"completed\":true}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Record with this date already exists"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Record edited successfully")
    void testEditRecordIsSuccess() throws Exception {
        String email = "user@example.com";
        String habitTitle = "Exercise";
        HabitRecordDTO recordDTOToUpdate = new HabitRecordDTO(
                LocalDate.of(2024, 10, 30), false);

        when(recordService.editRecord(eq(email), eq(habitTitle), any())).thenReturn(recordDTOToUpdate);

        mockMvc.perform(put("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2024-10-30\", \"completed\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.date").value("2024-10-30"))
                .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Date already exists, record editing failed")
    void testEditRecordConflict() throws Exception {
        String email = "user@example.com";
        String habitTitle = "Exercise";
        HabitRecordDTO recordDTOToUpdate = new HabitRecordDTO(
                LocalDate.of(2024, 10, 12), true);

        when(recordService.editRecord(eq(email), eq(habitTitle), any())).thenReturn(null);

        mockMvc.perform(put("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2024-10-12\", \"completed\":true}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Record with this completion status already exists"));
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Record deleted successfully")
    void testDeleteRecordIsSuccess() throws Exception {
        String email = "user@example.com";
        String habitTitle = "Exercise";
        HabitRecordDTO recordDTOToDelete = new HabitRecordDTO(
                LocalDate.of(2024, 10, 13), true);

        when(recordService.deleteRecord(eq(email), eq(habitTitle), any())).thenReturn(true);

        mockMvc.perform(delete("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2024-10-13\", \"completed\":true}"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Date not found, record deletion failed")
    void testDeleteRecordDateNotFound() throws Exception {
        String email = "user@example.com";
        String habitTitle = "Exercise";
        HabitRecordDTO recordDTOToDelete = new HabitRecordDTO(
                LocalDate.of(2024, 10, 14), true);

        when(recordService.deleteRecord(eq(email), eq(habitTitle), any())).thenReturn(false);

        mockMvc.perform(delete("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2024-10-14\", \"completed\":true}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Record not found"));
    }
}
