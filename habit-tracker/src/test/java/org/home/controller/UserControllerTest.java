package org.home.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.home.controller.api.UserController;
import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.model.User;
import org.home.service.UserService;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.home.model.Role.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@DisplayName("UserController test")
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UserDTO userDTO;
    private UserCreateDTO userCreateDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
        objectMapper = new ObjectMapper();
        userDTO = new UserDTO("Test User", "test@example.com");
        userCreateDTO = new UserCreateDTO("New User", "new@example.com", "password123");
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET: All users retrieved successfully by admin")
    void testGetAllUsers() throws Exception {
        Map<String, UserDTO> expectedUsers = Map.of("test@example.com", userDTO);
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        String response = mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, UserDTO> usersResponse = objectMapper.readValue(response, new TypeReference<>() { });
        assertThat(usersResponse).isEqualTo(expectedUsers);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    @DisplayName("PUT: User updated themself successfully")
    void testUpdateUserSelf() throws Exception {
        when(userService.editProfile(anyString(), any())).thenReturn(userDTO);

        String response = mockMvc.perform(put("/api/users/test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO userResponse = objectMapper.readValue(response, UserDTO.class);
        assertThat(userResponse).isEqualTo(userDTO);
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = "USER")
    @DisplayName("DELETE: User deleted themself successfully")
    void testDeleteUserSelf() throws Exception {
        when(userService.deleteUser("test@example.com")).thenReturn(true);

        mockMvc.perform(delete("/api/users/test@example.com"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("User blocked successfully by admin")
    void testAdminActionBlockUserSuccess() throws Exception {
        User user = new User("Ivan", "user@example.com", "ivanpass123", USER);
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        when(userService.blockUser(user)).thenReturn(true);

        mockMvc.perform(put("/api/users/block/user@example.com"))
                .andExpect(status().isOk());
    }
}
