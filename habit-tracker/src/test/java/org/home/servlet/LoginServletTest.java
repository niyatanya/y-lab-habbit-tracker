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
import java.io.StringReader;
import java.io.StringWriter;
import java.io.BufferedReader;

import static org.home.model.Role.USER;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@DisplayName("LoginServlet test")
public class LoginServletTest {

    private UserService userService;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private StringWriter responseWriter;
    private LoginServlet loginServlet;
    private AuthService authService;

    @BeforeEach
    public void setUp() throws Exception {
        userService = mock(UserService.class);
        authService = mock(AuthService.class);
        loginServlet = new LoginServlet(userService, authService);
        new AdminActionsServlet(userService);

        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        responseWriter = new StringWriter();

        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
        when(response.getCharacterEncoding()).thenReturn("UTF-8");
    }

    @DisplayName("Login successfull")
    @Test
    public void testSuccessfulLogin() throws IOException, ServletException {
        String email = "user@example.com";
        String password = "password";
        User user = new User("Login User", email, password, USER);
        String token = "testToken";

        when(request.getReader()).thenReturn(new BufferedReader(
                new StringReader("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")));
        when(userService.findUserByEmail(email)).thenReturn(user);
        when(userService.validatePassword(user, password)).thenReturn(true);
        when(authService.loginUser(user)).thenReturn(token);

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        assertTrue(responseWriter.toString().contains("\"token\":\"testToken\""));
    }

    @DisplayName("Do not login blocked user")
    @Test
    public void testLoginWithBlockedUser() throws IOException, ServletException {
        String email = "blockedUser@example.com";
        String password = "password";
        User user = new User("Blocked User", email, password, USER);
        user.setBlocked(true);

        when(request.getReader()).thenReturn(new BufferedReader(
                new StringReader("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")));
        when(userService.findUserByEmail(email)).thenReturn(user);
        when(userService.validatePassword(user, password)).thenReturn(true);

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @DisplayName("Do not login with incorrect password")
    @Test
    public void testIncorrectPassword() throws IOException, ServletException {
        String email = "user@example.com";
        String password = "wrongPassword";
        User user = new User("Wrongpass User", email, password, USER);

        when(request.getReader()).thenReturn(new BufferedReader(
                new StringReader("{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}")));
        when(userService.findUserByEmail(email)).thenReturn(user);
        when(userService.validatePassword(user, password)).thenReturn(false); // Wrong password

        loginServlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
