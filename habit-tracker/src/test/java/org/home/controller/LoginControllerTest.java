package org.home.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.home.controller.api.LoginController;
import org.home.dto.ErrorResponseDTO;
import org.home.dto.LoginInputDTO;
import org.home.dto.LoginOutputDTO;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

        String response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        LoginOutputDTO loginResponse = objectMapper.readValue(response, LoginOutputDTO.class);
        assertThat(loginResponse.getToken()).isNotNull();
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

        String response = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDTO)))
                .andExpect(status().isForbidden())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponseDTO errorResponse = objectMapper.readValue(response, ErrorResponseDTO.class);
        assertThat(errorResponse.getError()).isEqualTo("User is blocked. Access denied.");
    }
}
