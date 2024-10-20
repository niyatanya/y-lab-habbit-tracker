package org.home.service;

import org.home.config.DBConnectionProvider;
import org.home.model.User;
import org.home.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserService test")
class UserServiceTest {

    private static PostgreSQLContainer<?> testDb = new PostgreSQLContainer<>("postgres")
        .withInitScript("test-schema.sql");

    private UserService userService;

    @BeforeAll
    static void beforeAll() {
        testDb.start();
    }

    @AfterAll
    static void afterAll() {
        testDb.stop();
    }

    @BeforeEach
    void setUp() {
        DBConnectionProvider connectionProvider = new DBConnectionProvider(
                testDb.getJdbcUrl(),
                testDb.getUsername(),
                testDb.getPassword()
        );
        userService = new UserService();
        UserRepository userRepository = new UserRepository(connectionProvider);
    }

    @Test
    @DisplayName("Register new user with correct credentials")
    void shouldRegisterNewUserSuccessfully() {
        User user = userService.register("Catharina Mane", "kate@example.com", "password123");

        assertThat(user).isNotNull();
        assertThat(user.getName()).isEqualTo("Catharina Mane");
        assertThat(user.getEmail()).isEqualTo("kate@example.com");
        assertThat(user.getPassword()).isEqualTo("password123");
        assertThat(userService.getAllUsers()).containsKey("kate@example.com");
    }

    @Test
    @DisplayName("Fail to register new user with duplicate email")
    void shouldNotRegisterUserWithDuplicateEmail() {
        int initUsersCount = userService.getAllUsers().size();
        userService.register("John Doe", "john@example.com", "password123");

        User duplicateUser = userService.register("Jane Doe", "john@example.com", "password456");
        assertThat(duplicateUser).isNull();

        int usersCountAfterOperation = userService.getAllUsers().size();
        assertThat(initUsersCount).isEqualTo(usersCountAfterOperation);
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
        User user = userService.register("Jimmy Handriks", "jymmy@example.com", "password123");

        assertThat(userService.getAllUsers()).containsKey("jymmy@example.com");
        userService.editProfile(user, "Johnny", "johnny@example.com", "newpassword123");

        assertThat(user.getName()).isEqualTo("Johnny");
        assertThat(user.getEmail()).isEqualTo("johnny@example.com");
        assertThat(user.getPassword()).isEqualTo("newpassword123");
    }

    @Test
    @DisplayName("Edit user profile with existing email")
    void shouldNotEditProfileWithExistingEmail() {
        User user1 = userService.register("Bella Ostin", "bella@example.com", "password123");
        userService.register("Jane Smith", "jane@example.com", "password456");

        userService.editProfile(user1, "John Updated", "jane@example.com", "newpassword");

        assertThat(user1.getEmail()).isEqualTo("bella@example.com");
    }

    @Test
    @DisplayName("Delete user")
    void shouldDeleteUserSuccessfully() {
        User user = userService.register("Samantha Smith", "samsam@example.com", "password123");
        assertThat(userService.getAllUsers()).containsKey("samsam@example.com");

        userService.deleteUser(user);
        assertThat(userService.getAllUsers()).doesNotContainKey("samsam@example.com");
    }
}
