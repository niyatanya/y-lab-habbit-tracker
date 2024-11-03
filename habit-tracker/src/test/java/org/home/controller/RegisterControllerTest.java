package org.home.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.home.controller.api.RegisterController;
import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.service.UserService;

import org.springframework.http.MediaType;
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

@DisplayName("RegisterController test")
public class RegisterControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private RegisterController registerController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(registerController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("User registered successfully")
    void testRegisterUserIsSuccessful() throws Exception {
        String email = "newuser@example.com";
        String name = "New User";
        String password = "password";

        UserCreateDTO userCreateDTO = new UserCreateDTO(name, email, password);
        UserDTO newUserDTO = new UserDTO(name, email);

        when(userService.register(any())).thenReturn(newUserDTO);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.name").value(name));
    }

    @Test
    @DisplayName("Email already exists, register fails")
    void testRegisterUserEmailAlreadyExists() throws Exception {
        String email = "existinguser@example.com";
        String name = "Existing User";
        String password = "password";

        UserCreateDTO userCreateDTO = new UserCreateDTO(name, email, password);

        when(userService.register(any())).thenReturn(null);

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email is already registered"));
    }
}
