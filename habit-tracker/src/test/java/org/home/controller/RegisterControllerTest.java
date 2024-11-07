package org.home.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.home.controller.api.RegisterController;
import org.home.dto.ErrorResponseDTO;
import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RegisterController.class)
@DisplayName("RegisterController test")
public class RegisterControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("User registered successfully")
    void testRegisterUserIsSuccessful() throws Exception {
        String email = "newuser@example.com";
        String name = "New User";
        String password = "password";

        UserCreateDTO userCreateDTO = new UserCreateDTO(name, email, password);
        UserDTO newUserDTO = new UserDTO(name, email);

        when(userService.register(any())).thenReturn(newUserDTO);

        String response = mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO userResponse = objectMapper.readValue(response, UserDTO.class);
        assertThat(userResponse).isEqualTo(newUserDTO);
    }

    @Test
    @DisplayName("Email already exists, register fails")
    void testRegisterUserEmailAlreadyExists() throws Exception {
        String email = "existinguser@example.com";
        String name = "Existing User";
        String password = "password";

        UserCreateDTO userCreateDTO = new UserCreateDTO(name, email, password);

        when(userService.register(any())).thenReturn(null);

        String response = mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isConflict())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ErrorResponseDTO errorResponse = objectMapper.readValue(response, ErrorResponseDTO.class);
        assertThat(errorResponse.getError()).isEqualTo("Email is already registered");
    }
}
