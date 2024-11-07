package org.home.service;

import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.model.User;
import org.home.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = JdbcRepositoriesAutoConfiguration.class)
@DisplayName("UserService test")
class UserServiceTest {

    @Container
    private static PostgreSQLContainer<?> testDb = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("test-db")
            .withUsername("test-db-username")
            .withPassword("test-db-pass")
            .withInitScript("test-schema.sql");

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", testDb::getJdbcUrl);
        registry.add("spring.datasource.username", testDb::getUsername);
        registry.add("spring.datasource.password", testDb::getPassword);
    }

    @Test
    @DisplayName("Register new user with correct credentials")
    void shouldRegisterNewUserSuccessfully() {
        UserDTO user = userService.register(
                new UserCreateDTO("Catharina Mane", "kate@example.com", "password123"));

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("Catharina Mane");
        assertThat(user.getEmail()).isEqualTo("kate@example.com");
        assertThat(userService.getAllUsers()).containsKey("kate@example.com");
    }

    @Test
    @DisplayName("Fail to register new user with duplicate email")
    void shouldNotRegisterUserWithDuplicateEmail() {
        int initUsersCount = userService.getAllUsers().size();
        userService.register(new UserCreateDTO("John Doe", "john@example.com", "password123"));

        UserDTO duplicateUser = userService.register(
                new UserCreateDTO("Jane Doe", "john@example.com", "password123"));
        assertThat(duplicateUser).isNull();

        int usersCountAfterOperation = userService.getAllUsers().size();
        assertThat(initUsersCount).isEqualTo(usersCountAfterOperation);
    }

    @Test
    @DisplayName("Login user with correct credentials")
    void shouldLoginUserSuccessfully() {
        userService.register(new UserCreateDTO("John Doe", "john@example.com", "password123"));

        User loggedInUser = userService.login("john@example.com", "password123");

        assertThat(loggedInUser).isNotNull();
        assertThat(loggedInUser.getEmail()).isEqualTo("john@example.com");
        assertThat(loggedInUser.getPassword()).isEqualTo("password123");
    }

    @Test
    @DisplayName("Fail to login a user with incorrect password")
    void shouldNotLoginWithIncorrectPassword() {
        userService.register(new UserCreateDTO("John Doe", "john@example.com", "password123"));

        User loggedInUser = userService.login("john@example.com", "wrongpassword");
        assertThat(loggedInUser).isNull();
    }

    @Test
    @DisplayName("Edit user profile with valid data")
    void shouldEditProfileSuccessfully() {
        userService.register(
                new UserCreateDTO("Jimmy Handriks", "jymmy@example.com", "password123"));
        assertThat(userService.getAllUsers()).containsKey("jymmy@example.com");

        UserDTO updatedUser = userService.editProfile("jymmy@example.com",
                new UserCreateDTO("Johnny", "johnny@example.com", "newpassword123"));

        assertThat(updatedUser.getName()).isEqualTo("Johnny");
        assertThat(updatedUser.getEmail()).isEqualTo("johnny@example.com");
        assertThat(userRepository.findByEmail("johnny@example.com").get().getPassword())
                .isEqualTo("newpassword123");
    }

    @Test
    @DisplayName("Edit user profile with existing email")
    void shouldNotEditProfileWithExistingEmail() {
        UserDTO user1 = userService.register(
                new UserCreateDTO("Bella Ostin", "bella@example.com", "password123"));
        userService.register(new UserCreateDTO("Jane Smith", "jane@example.com", "password456"));

        userService.editProfile("bella@example.com",
                new UserCreateDTO("John Updated", "jane@example.com", "newpassword"));

        assertThat(user1.getEmail()).isEqualTo("bella@example.com");
    }

    @Test
    @DisplayName("Delete user")
    void shouldDeleteUserSuccessfully() {
        UserDTO user = userService.register(
                new UserCreateDTO("Samantha Smith", "samsam@example.com", "password123"));
        assertThat(userService.getAllUsers()).containsKey("samsam@example.com");

        userService.deleteUser(user.getEmail());
        assertThat(userService.getAllUsers()).doesNotContainKey("samsam@example.com");
    }
}
