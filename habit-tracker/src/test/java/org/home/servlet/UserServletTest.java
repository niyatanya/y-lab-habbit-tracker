package org.home.servlet;

import org.home.service.AuthService;
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
import java.util.Map;

import org.home.dto.UserDTO;

import static org.home.model.Role.ADMIN;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.eq;

@DisplayName("UserServlet test")
public class UserServletTest {

    private UserServlet userServlet;
    private UserService userService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private AuthService.UserSession userSession;

    @BeforeEach
    public void setUp() throws Exception {
        userService = mock(UserService.class);
        userServlet = new UserServlet(userService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(response.getCharacterEncoding()).thenReturn("UTF-8");

        userSession = mock(AuthService.UserSession.class);
        when(request.getAttribute("userSession")).thenReturn(userSession);
    }

    @DisplayName("GET: All users retrieved successfully")
    @Test
    public void testDoGetAllUsersRetrievedSuccessfully() throws Exception {
        when(userSession.getRole()).thenReturn(ADMIN);
        Map<String, UserDTO> users = Map.of(
                "mary@example.com", new UserDTO("Mary", "mary@example.com"),
                "james@example.com", new UserDTO("James", "james@example.com")
        );
        when(userService.getAllUsers()).thenReturn(users);

        userServlet.doGet(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("\"name\":\"Mary\""));
        assertTrue(responseWriter.toString().contains("\"name\":\"James\""));
    }

    @DisplayName("PUT: User updated successfully")
    @Test
    public void testDoPutUserUpdatedSuccessfully() throws Exception {
        String email = "anna@example.com";
        String pathInfo = "/" + email;
        String updatedUserJson = "{\"name\": \"Anna Updated\", \"email\": \"anna.updated@example.com\","
                + "\"password\": \"newPassword\"}";

        when(userSession.getEmail()).thenReturn(email);
        when(request.getPathInfo()).thenReturn(pathInfo);
        when(request.getReader()).thenReturn(new BufferedReader(new StringReader(updatedUserJson)));

        UserDTO updatedUserDTO = new UserDTO("Anna Updated", "anna.updated@example.com");
        when(userService.editProfile(eq(email), any())).thenReturn(updatedUserDTO);

        userServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("\"name\":\"Anna Updated\""));
    }

    @DisplayName("DELETE: User deleted successfully")
    @Test
    public void testDoDeleteUserDeletedSuccessfully() throws Exception {
        String email = "sam@example.com";
        String pathInfo = "/" + email;

        when(userSession.getEmail()).thenReturn(email);
        when(request.getPathInfo()).thenReturn(pathInfo);
        when(userService.deleteUser(email)).thenReturn(true);

        userServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    @DisplayName("DELETE: Admin user can not be deleted")
    @Test
    public void testDoDeleteAdminUserDeletionForbidden() throws Exception {
        String email = "admin.example@example.com";
        String pathInfo = "/" + email;

        when(userSession.getRole()).thenReturn(ADMIN);
        when(userSession.getEmail()).thenReturn(email);
        when(request.getPathInfo()).thenReturn(pathInfo);
        when(userService.deleteUser(email)).thenReturn(false);

        userServlet.doDelete(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(responseWriter.toString().contains("Cannot delete an admin user."));
    }
}
