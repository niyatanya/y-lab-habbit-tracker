package org.home.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.home.controller.api.LoginController;
import org.home.dto.ErrorResponseDTO;
import org.home.dto.LoginInputDTO;
import org.home.dto.LoginOutputDTO;
import org.home.model.User;
import org.home.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = LoginController.class)
@DisplayName("LoginController test")
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private AuthenticationManager authenticationManager;

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
