package org.home.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.home.controller.api.LoginController;
import org.home.dto.LoginInputDTO;
import org.home.model.User;
import org.home.service.UserService;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("LoginController test")
public class LoginControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private LoginController loginController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("User login successfully")
    void testLoginUserIsSuccessful() throws Exception {
        String username = "test@example.com";
        String password = "password";

        LoginInputDTO inputDTO = new LoginInputDTO(username, password);
        User user = new User();
        user.setEmail(username);
        user.setBlocked(false);

        when(userService.findUserByEmail(username)).thenReturn(user);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    @DisplayName("BLocked user login fail")
    void testLoginUserForBlockedUser() throws Exception {
        String username = "blocked@example.com";
        String password = "password";

        LoginInputDTO inputDTO = new LoginInputDTO(username, password);
        User blockedUser = new User();
        blockedUser.setEmail(username);
        blockedUser.setBlocked(true);

        when(userService.findUserByEmail(username)).thenReturn(blockedUser);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("User is blocked. Access denied."));
    }
}
