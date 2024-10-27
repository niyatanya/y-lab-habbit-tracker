package org.home.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.home.dto.StatisticsInputDTO;
import org.home.service.AuthService;
import org.home.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.StringReader;
import java.io.BufferedReader;
import java.util.Map;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@DisplayName("StatisticsServlet test")
public class StatisticsServletTest {

    private StatisticsServlet statisticsServlet;
    private StatisticsService statisticsService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private ObjectMapper objectMapper;
    private AuthService.UserSession userSession;

    @BeforeEach
    public void setUp() throws Exception {
        statisticsService = mock(StatisticsService.class);
        statisticsServlet = new StatisticsServlet(statisticsService);

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

    @DisplayName("Statistics obtained successfully")
    @Test
    public void testDoGetSuccess() throws Exception {
        when(request.getPathInfo()).thenReturn("/test@example.com");
        when(userSession.getEmail()).thenReturn("test@example.com");

        StatisticsInputDTO inputDTO = new StatisticsInputDTO(
                "test@example.com",
                "Exercise",
                LocalDate.of(2024, 1, 1),
                LocalDate.of(2024, 12, 31));
        String jsonRequest = objectMapper.writeValueAsString(inputDTO);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(jsonRequest)));

        Map<String, Map<String, String>> responseMap = Map.of(
                "Progress Report for Habit: Exercise",
                Map.of("Period: ", "2023-01-01 to 2023-12-31",
                        "Total intervals: ", "365",
                        "Successful intervals: ", "200",
                        "Success rate: ", "54.79%",
                        "Current streak: ", "30 intervals")
        );
        when(statisticsService.generateProgressReport(any(StatisticsInputDTO.class))).thenReturn(responseMap);

        statisticsServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertEquals(objectMapper.writeValueAsString(responseMap), responseWriter.toString());
    }
}
