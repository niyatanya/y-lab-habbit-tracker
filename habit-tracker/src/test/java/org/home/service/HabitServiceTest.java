package org.home.service;

import org.home.config.DBConnectionProvider;
import org.home.model.Frequency;
import org.home.model.Habit;
import org.home.model.User;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HabitService test")
class HabitServiceTest {

    private static PostgreSQLContainer<?> testDb = new PostgreSQLContainer<>("postgres")
            .withInitScript("test-schema.sql");

    private UserService userService;
    private HabitService habitService;
    private User user;

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
        user = userService.findUserByEmail("tu@example.com");

        habitService = new HabitService();
        HabitRepository habitRepository = new HabitRepository(connectionProvider);
    }

    @Test
    @DisplayName("Create habit")
    void testCreateHabit() {
        String title = "Do push ups";
        String description = "Do 10 push ups every day";
        Frequency frequency = Frequency.DAILY;

        Habit createdHabit = habitService.createHabit(user, title, description, frequency);

        assertThat(createdHabit.getTitle()).isEqualTo(title);
        assertThat(createdHabit.getDescription()).isEqualTo(description);
        assertThat(createdHabit.getFrequency()).isEqualTo(frequency);
        assertThat(habitService.getAllHabits(user)).containsKey(title);
    }

    @Test
    @DisplayName("Edit habit")
    void testEditHabit() {
        String oldTitle = "Read a book";
        String newTitle = "Read a novel";
        String newDescription = "Read for 1 hour";
        Frequency newFrequency = Frequency.WEEKLY;

        Habit habit = habitService.createHabit(user, oldTitle, "Description", Frequency.DAILY);
        assertThat(habit.getTitle()).isEqualTo(oldTitle);

        habitService.editHabit(user, oldTitle, newTitle, newDescription, newFrequency);

        Habit editedHabit = habitService.findByTitleAndUserId(user, newTitle);

        assertThat(editedHabit).isNotNull();
        assertThat(editedHabit.getDescription()).isEqualTo(newDescription);
        assertThat(editedHabit.getFrequency()).isEqualTo(newFrequency);
    }

    @Test
    @DisplayName("Delete habit")
    void testDeleteHabit() {
        String title = "Drink water";
        Habit habit = habitService.createHabit(user, title, "Description", Frequency.DAILY);
        assertThat(habitService.getAllHabits(user)).containsKey(title);

        habitService.deleteHabit(user, title);
        assertThat(habitService.getAllHabits(user)).doesNotContainKey(title);
    }
}
