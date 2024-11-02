package org.home.servlet;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.home.dto.HabitDTO;
import org.home.service.AuthService;
import org.home.service.HabitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.BufferedReader;
import java.util.Map;

import static org.home.model.Frequency.DAILY;
import static org.home.model.Frequency.WEEKLY;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

@DisplayName("HabitServlet test")
public class HabitServletTest {

    private HabitServlet habitServlet;
    private HabitService habitService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private AuthService.UserSession userSession;

    @BeforeEach
    public void setUp() throws Exception {
        habitService = mock(HabitService.class);
        habitServlet = new HabitServlet(habitService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(response.getCharacterEncoding()).thenReturn("UTF-8");

        userSession = mock(AuthService.UserSession.class);
        when(request.getAttribute("userSession")).thenReturn(userSession);
    }

    @DisplayName("GET: All habits of a user retrieved successfully")
    @Test
    public void testDoGetAllHabitsRetrievedSuccessfully() throws Exception {
        String email = "billy@example.com";
        String pathInfo = "/" + email;
        when(userSession.getEmail()).thenReturn(email);
        when(request.getPathInfo()).thenReturn(pathInfo);

        Map<String, HabitDTO> habitMap = Map.of(
                "Count calories",
                new HabitDTO("Count calories", "Count calories every day", DAILY),
                "Visit grandma",
                new HabitDTO("Visit grandma", "Visit grandma every week", WEEKLY)
        );
        when(habitService.getAllHabits(any())).thenReturn(habitMap);

        habitServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("\"title\":\"Count calories\""));
        assertTrue(responseWriter.toString().contains("\"title\":\"Visit grandma\""));
    }

    @DisplayName("POST: Habit saved successfully")
    @Test
    public void testDoPostHabitCreatedSuccessfully() throws Exception {
        String email = "robert@example.com";
        String pathInfo = "/" + email;
        when(userSession.getEmail()).thenReturn(email);
        when(request.getPathInfo()).thenReturn(pathInfo);

        String habitJson = "{\"title\": \"Read\", \"description\": \"Read books daily\", \"frequency\": \"DAILY\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(habitJson)));

        HabitDTO habitDTO = new HabitDTO("Read", "Read books daily", DAILY);
        when(habitService.createHabit(any(), any())).thenReturn(habitDTO);

        habitServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertTrue(responseWriter.toString().contains("\"title\":\"Read\""));
    }

    @DisplayName("POST: Title already exists, new habit is not saved")
    @Test
    public void testDoPostHabitAlreadyExists() throws Exception {
        String email = "mike@example.com";
        String pathInfo = "/" + email;
        when(userSession.getEmail()).thenReturn(email);
        when(request.getPathInfo()).thenReturn(pathInfo);

        String habitJson = "{\"title\": \"Swim\", \"description\": \"Swim in pool weekly\", \"frequency\": \"WEEKLY\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(habitJson)));

        when(habitService.createHabit(eq(email), any())).thenReturn(null);

        habitServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        assertTrue(responseWriter.toString().contains("Habit with this title already exists"));
    }

    @DisplayName("PUT: Habit updated successfully")
    @Test
    public void testDoPutHabitEditedSuccessfully() throws Exception {
        String title = "Do a lesson";
        String email = "margaret@example.com";
        String pathInfo = "/" + email + "/" + title;
        when(userSession.getEmail()).thenReturn(email);
        when(request.getPathInfo()).thenReturn(pathInfo);

        String updatedHabitJson =
                "{\"title\": \"Study English\","
               + "\"description\": \"Do English lessons every day\","
               + "\"frequency\": \"DAILY\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(updatedHabitJson)));

        HabitDTO updatedHabitDTO = new HabitDTO("Study English", "Do English lessons every day", DAILY);
        when(habitService.editHabit(any(), eq(title), any())).thenReturn(updatedHabitDTO);

        habitServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("\"title\":\"Study English\""));
    }

    @DisplayName("DELETE: Habit deleted successfully")
    @Test
    public void testDoDeleteHabitDeletedSuccessfully() throws Exception {
        String title = "Knit";
        String email = "lana@example.com";
        String pathInfo = "/" + email + "/" + title;
        when(userSession.getEmail()).thenReturn(email);
        when(request.getPathInfo()).thenReturn(pathInfo);

        when(habitService.deleteHabit(any(), eq(title))).thenReturn(true);

        habitServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @DisplayName("DELETE: Habit was not found")
    @Test
    public void testDoDeleteHabitNotFound() throws Exception {
        String title = "Wash dishes";
        String email = "carl@example.com";
        String pathInfo = "/" + email + "/" + title;
        when(userSession.getEmail()).thenReturn(email);
        when(request.getPathInfo()).thenReturn(pathInfo);

        when(habitService.deleteHabit(any(), eq(title))).thenReturn(false);

        habitServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        assertTrue(responseWriter.toString().contains("Habit not found"));
    }
}
