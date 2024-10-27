package org.home.servlet;

import org.home.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;

import org.home.dto.UserDTO;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@DisplayName("RegisterServlet test")
public class RegisterServletTest {

    private RegisterServlet registerServlet;
    private UserService userService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        userService = mock(UserService.class);
        registerServlet = new RegisterServlet(userService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(response.getCharacterEncoding()).thenReturn("UTF-8");
    }

    @DisplayName("POST: User registered successfully")
    @Test
    public void testDoPostUserRegisteredSuccessfully() throws Exception {
        String userJson = "{\"name\": \"Jahn\", \"email\": \"jahn@example.com\", \"password\": \"password\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(userJson)));

        UserDTO userDTO = new UserDTO("Jahn", "jahn@example.com");
        when(userService.register(any())).thenReturn(userDTO);

        registerServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CREATED);
        assertTrue(responseWriter.toString().contains("\"name\":\"Jahn\""));
    }

    @DisplayName("POST: Email already exists, new user is not registered")
    @Test
    public void testDoPostUserEmailAlreadyExists() throws Exception {
        String userJson = "{\"name\": \"Joanne\", \"email\": \"joanne@example.com\", \"password\": \"password\"}";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(userJson)));

        when(userService.register(any())).thenReturn(null);

        registerServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        assertTrue(responseWriter.toString().contains("Email is already registered"));
    }

    @DisplayName("POST: Invalid input, new user is not registered")
    @Test
    public void testDoPostInvalidInput() throws Exception {
        String invalidJson = "invalid_json";
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(invalidJson)));

        registerServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseWriter.toString().contains("Invalid input"));
    }
}
