package org.home.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.home.model.User;
import org.home.service.AuthService;
import org.home.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.home.model.Role.ADMIN;
import static org.home.model.Role.USER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@DisplayName("AdminActionsServlet test")
public class AdminActionsServletTest {
    private UserService userService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private AdminActionsServlet adminActionsServlet;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        userService = mock(UserService.class);
        adminActionsServlet = new AdminActionsServlet(userService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(response.getCharacterEncoding()).thenReturn("UTF-8");
    }

    @DisplayName("User blocked successfully")
    @Test
    public void testBlockUserSuccess() throws IOException, ServletException {
        String email = "user@example.com";
        User user = new User("Test User", email, "test_pass", USER);

        when(request.getPathInfo()).thenReturn("/block/" + email);
        when(request.getAttribute("userSession")).thenReturn(
                new AuthService.UserSession("admin@example.com", ADMIN));
        when(userService.findUserByEmail(email)).thenReturn(user);
        when(userService.blockUser(user)).thenReturn(true);

        adminActionsServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("User blocked successfully."));
    }

    @DisplayName("Can not block admin user")
    @Test
    public void testBlockAdminUser() throws IOException, ServletException {
        String email = "admin@example.com";
        User adminUser = new User("Admin User", email, "admin_pass", ADMIN);

        when(request.getPathInfo()).thenReturn("/block/" + email);
        when(request.getAttribute("userSession")).thenReturn(
                new AuthService.UserSession("admin@example.com", ADMIN));
        when(userService.findUserByEmail(email)).thenReturn(adminUser);

        adminActionsServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(responseWriter.toString().contains("Cannot block an admin user."));
    }

    @DisplayName("User unblocked successfully")
    @Test
    public void testUnblockUserSuccess() throws IOException, ServletException {
        String email = "user@example.com";
        User user = new User("Test User 2", email, "another_pass", USER);

        when(request.getPathInfo()).thenReturn("/unblock/" + email);
        when(request.getAttribute("userSession")).thenReturn(
                new AuthService.UserSession("admin@example.com", ADMIN));
        when(userService.findUserByEmail(email)).thenReturn(user);
        when(userService.unblockUser(user)).thenReturn(true);

        adminActionsServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("User unblocked successfully."));
    }

    @DisplayName("Non-admins are not allowed")
    @Test
    public void testForbiddenAccessNonAdmin() throws IOException, ServletException {
        when(request.getPathInfo()).thenReturn("/block/user@example.com");
        when(request.getAttribute("userSession")).thenReturn(
                new AuthService.UserSession("user@example.com", USER));

        adminActionsServlet.doPut(request, response);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(responseWriter.toString().contains("Access denied. Admins only."));
    }
}
