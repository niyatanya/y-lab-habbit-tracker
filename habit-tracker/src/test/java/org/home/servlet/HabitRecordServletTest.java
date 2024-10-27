package org.home.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.home.dto.HabitRecordDTO;
import org.home.service.AuthService;
import org.home.service.HabitRecordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@DisplayName("HabitRecordServlet test")
public class HabitRecordServletTest {

    private HabitRecordServlet recordServlet;
    private HabitRecordService recordService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private ObjectMapper objectMapper;
    private AuthService.UserSession userSession;

    @BeforeEach
    public void setUp() throws Exception {
        recordService = mock(HabitRecordService.class);
        recordServlet = new HabitRecordServlet(recordService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(response.getCharacterEncoding()).thenReturn("UTF-8");

        userSession = mock(AuthService.UserSession.class);
        when(request.getAttribute("userSession")).thenReturn(userSession);
    }

    @DisplayName("GET: All records of a habit retrieved successfully")
    @Test
    public void testDoGetAllRecordsRetrievedSuccessfully() throws Exception {
        when(userSession.getEmail()).thenReturn("user@example.com");
        when(request.getPathInfo()).thenReturn("/user@example.com/habitTitle");

        Map<LocalDate, HabitRecordDTO> recordMap = Map.of(
                LocalDate.of(2024, 10, 16),
                new HabitRecordDTO(LocalDate.of(2024, 10, 16), true),
                LocalDate.of(2024, 10, 17),
                new HabitRecordDTO(LocalDate.of(2024, 10, 17), false)
        );

        when(recordService.getAllRecords(any(), any())).thenReturn(recordMap);

        recordServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        String expectedResponse = objectMapper.writeValueAsString(recordMap);
        assertEquals(expectedResponse, responseWriter.toString());
    }

    @DisplayName("POST: Record saved successfully")
    @Test
    public void testDoPostRecordCreatedSuccessfully() throws Exception {
        when(userSession.getEmail()).thenReturn("user@example.com");
        when(request.getPathInfo()).thenReturn("/user@example.com/habitTitle");

        String recordJson = objectMapper.writeValueAsString(
                new HabitRecordDTO(LocalDate.of(2024, 10, 1), true));

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(recordJson)));

        HabitRecordDTO recordDTO = new HabitRecordDTO(LocalDate.of(2024, 10, 1), true);
        when(recordService.createRecord(any(), any(), any())).thenReturn(recordDTO);

        recordServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertEquals(recordJson, responseWriter.toString());
    }

    @DisplayName("PUT: Record updated successfully")
    @Test
    public void testDoPutRecordEditedSuccessfully() throws Exception {
        when(userSession.getEmail()).thenReturn("user@example.com");
        when(request.getPathInfo()).thenReturn("/user@example.com/habitTitle");

        String updatedRecordJson = objectMapper.writeValueAsString(
                new HabitRecordDTO(LocalDate.of(2024, 10, 2), false));

        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(updatedRecordJson)));

        HabitRecordDTO updatedRecordDTO = new HabitRecordDTO(LocalDate.of(
                2024, 10, 2), false);
        when(recordService.editRecord(any(), any(), any())).thenReturn(updatedRecordDTO);

        recordServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals(updatedRecordJson, responseWriter.toString());
    }

    @DisplayName("DELETE: Record deleted successfully")
    @Test
    public void testDoDeleteRecordDeletedSuccessfully() throws Exception {
        when(userSession.getEmail()).thenReturn("user@example.com");
        when(request.getPathInfo()).thenReturn("/user@example.com/habitTitle");

        String recordJsonToDelete = objectMapper.writeValueAsString(
                new HabitRecordDTO(LocalDate.of(2024, 10, 3), false));
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(recordJsonToDelete)));

        when(recordService.deleteRecord(any(), any(), any())).thenReturn(true);

        recordServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }
}
