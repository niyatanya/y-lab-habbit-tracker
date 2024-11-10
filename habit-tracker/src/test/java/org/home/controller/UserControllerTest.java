package org.home.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.home.controller.api.UserController;
import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.model.User;
import org.home.service.UserService;
import java.util.Map;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.home.model.Role.ADMIN;
import static org.home.model.Role.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@WebMvcTest(controllers = UserController.class)
@DisplayName("UserController test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDTO userDTO;
    private UserCreateDTO userCreateDTO;

    private Claims adminClaims;
    private Claims userClaims;

    @BeforeEach
    void setup() {
        userDTO = new UserDTO("Test User", "test@example.com");
        userCreateDTO = new UserCreateDTO("New User", "new@example.com", "password123");

        adminClaims = Jwts.claims();
        adminClaims.put("username", "admin@example.com");
        adminClaims.put("role", ADMIN);

        userClaims = Jwts.claims();
        userClaims.put("username", "test@example.com");
        userClaims.put("role", USER);
    }

    @Test
    @DisplayName("GET: All users retrieved successfully by admin")
    void testGetAllUsers() throws Exception {
        Map<String, UserDTO> expectedUsers = Map.of("test@example.com", userDTO);
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        String response = mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .requestAttr("claims", adminClaims))
                .andExpect(status().isOk())
                .andExpect(header().string("X-Total-Count", "1"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, UserDTO> usersResponse = objectMapper.readValue(response, new TypeReference<>() { });
        assertThat(usersResponse).isEqualTo(expectedUsers);
    }

    @Test
    @DisplayName("GET: Forbidden to retrieve users for non-admin user")
    void testGetAllUsersForbiddenForNonAdmin() throws Exception {
        Map<String, UserDTO> expectedUsers = Map.of("test@example.com", userDTO);
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        mockMvc.perform(get("/api/users")
                        .accept(MediaType.APPLICATION_JSON)
                        .requestAttr("claims", userClaims))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("PUT: User updated themself successfully")
    void testUpdateUserSelf() throws Exception {
        when(userService.editProfile(anyString(), any())).thenReturn(userDTO);

        String response = mockMvc.perform(put("/api/users/test@example.com")
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("claims", userClaims)
                        .content(objectMapper.writeValueAsString(userCreateDTO)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDTO userResponse = objectMapper.readValue(response, UserDTO.class);
        assertThat(userResponse).isEqualTo(userDTO);
    }

    @Test
    @DisplayName("DELETE: User deleted themself successfully")
    void testDeleteUserSelf() throws Exception {
        when(userService.deleteUser("test@example.com")).thenReturn(true);

        mockMvc.perform(delete("/api/users/test@example.com")
                        .requestAttr("claims", userClaims))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("User blocked successfully by admin")
    void testAdminActionBlockUserSuccess() throws Exception {
        User user = new User("Ivan", "user@example.com", "ivanpass123", USER);
        when(userService.findUserByEmail("user@example.com")).thenReturn(user);
        when(userService.blockUser(user)).thenReturn(true);

        mockMvc.perform(put("/api/users/block/user@example.com")
                        .requestAttr("claims", adminClaims))
                .andExpect(status().isOk());
    }
}
