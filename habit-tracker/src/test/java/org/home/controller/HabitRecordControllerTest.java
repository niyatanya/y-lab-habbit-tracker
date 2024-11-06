package org.home.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.home.controller.api.HabitRecordController;
import org.home.dto.ErrorResponseDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@DisplayName("HabitRecordController test")
public class HabitRecordControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private HabitRecordService recordService;

    @InjectMocks
    private HabitRecordController recordController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(recordController).build();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
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

        String response = mockMvc.perform(get("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "2"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<LocalDate, HabitRecordDTO> recordsResponse = objectMapper.readValue(response, new TypeReference<>() { });
        assertThat(recordsResponse).isEqualTo(records);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Record created successfully")
    void testCreateRecordIsSuccess() throws Exception {
        String email = "user@example.com";
        String habitTitle = "Exercise";
        HabitRecordDTO recordDTO = new HabitRecordDTO(LocalDate.of(2024, 11, 3), true);

        when(recordService.createRecord(eq(email), eq(habitTitle), any())).thenReturn(recordDTO);

        String response = mockMvc.perform(post("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2024-11-03\", \"completed\":true}"))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        HabitRecordDTO recordResponse = objectMapper.readValue(response, HabitRecordDTO.class);
        assertThat(recordResponse).isEqualTo(recordDTO);
    }

    @Test
    @WithMockUser(username = "user@example.com", roles = "USER")
    @DisplayName("Date already exists, record creation failed")
    void testCreateRecordConflict() throws Exception {
        String email = "user@example.com";
        String habitTitle = "Exercise";
        HabitRecordDTO recordDTO = new HabitRecordDTO(LocalDate.of(2024, 11, 3), true);

        when(recordService.createRecord(eq(email), eq(habitTitle), any())).thenReturn(null);

        String response = mockMvc.perform(post("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2024-11-03\", \"completed\":true}"))
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponseDTO errorResponse = objectMapper.readValue(response, ErrorResponseDTO.class);
        assertThat(errorResponse.getError()).isEqualTo("Record with this date already exists");
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

        String response = mockMvc.perform(put("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2024-10-30\", \"completed\":false}"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        HabitRecordDTO recordResponse = objectMapper.readValue(response, HabitRecordDTO.class);
        assertThat(recordResponse).isEqualTo(recordDTOToUpdate);
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

        String response = mockMvc.perform(put("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2024-10-12\", \"completed\":true}"))
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponseDTO errorResponse = objectMapper.readValue(response, ErrorResponseDTO.class);
        assertThat(errorResponse.getError()).isEqualTo("Record with this completion status already exists");
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

        String response = mockMvc.perform(delete("/api/records/{email}/{habitTitle}", email, habitTitle)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2024-10-14\", \"completed\":true}"))
                .andExpect(status().isNotFound())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponseDTO errorResponse = objectMapper.readValue(response, ErrorResponseDTO.class);
        assertThat(errorResponse.getError()).isEqualTo("Record not found");
    }
}
