package org.home.service;

import org.home.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserService test")
class UserServiceTest {

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService();
    }

    @Test
    @DisplayName("Register new user with correct credentials")
    void shouldRegisterNewUserSuccessfully() {
        User user = userService.register("John Doe", "john@example.com", "password123");

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("John Doe");
        assertThat(user.getEmail()).isEqualTo("john@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(userService.getAllUsers()).contains(user);
    }

    @Test
    @DisplayName("Fail to register new user with duplicate email")
    void shouldNotRegisterUserWithDuplicateEmail() {
        userService.register("John Doe", "john@example.com", "password123");

        User duplicateUser = userService.register("Jane Doe", "john@example.com", "password456");

        assertThat(duplicateUser).isNull();
        assertThat(userService.getAllUsers()).hasSize(1);
    }

    @Test
    @DisplayName("Login user with correct credentials")
    void shouldLoginUserSuccessfully() {
        userService.register("John Doe", "john@example.com", "password123");

        User loggedInUser = userService.login("john@example.com", "password123");

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo("john@example.com");
        assertThat(loggedInUser.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("Fail to login a user with incorrect password")
    void shouldNotLoginWithIncorrectPassword() {
        userService.register("John Doe", "john@example.com", "password123");

        User loggedInUser = userService.login("john@example.com", "wrongpassword");

        assertThat(loggedInUser).isNull();
    }

    @Test
    @DisplayName("Edit user profile with valid data")
    void shouldEditProfileSuccessfully() {
        User user = userService.register("John Doe", "john@example.com", "password123");

        userService.editProfile(user, "Johnny", "johnny@example.com", "newpassword123");

        assertThat(user.getName()).isEqualTo("Johnny");
        assertThat(user.getEmail()).isEqualTo("johnny@example.com");
        assertThat(user.getPassword()).isEqualTo("newpassword123");
    }

    @Test
    @DisplayName("Edit user profile with existing email")
    void shouldNotEditProfileWithExistingEmail() {
        User user1 = userService.register("John Doe", "john@example.com", "password123");
        userService.register("Jane Smith", "jane@example.com", "password456");

        userService.editProfile(user1, "John Updated", "jane@example.com", "newpassword");

        assertThat(user1.getEmail()).isEqualTo("john@example.com");
    }

    @Test
    @DisplayName("Delete user")
    void shouldDeleteUserSuccessfully() {
        User user = userService.register("John Doe", "john@example.com", "password123");

        userService.deleteUser(user);

        assertThat(userService.getAllUsers()).doesNotContain(user);
    }
}
