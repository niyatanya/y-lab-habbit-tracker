package org.home.service;

import org.home.dto.HabitDTO;
import org.home.model.Frequency;
import org.home.model.User;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
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
@DisplayName("HabitService test")
class HabitServiceTest {

    @Container
    private static PostgreSQLContainer<?> testDb = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("test-db")
            .withUsername("test-db-username")
            .withPassword("test-db-pass")
            .withInitScript("test-schema.sql");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private HabitService habitService;

    private User user;

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", testDb::getJdbcUrl);
        registry.add("spring.datasource.username", testDb::getUsername);
        registry.add("spring.datasource.password", testDb::getPassword);
    }

    @BeforeEach
    void setUp() {
        user = userService.findUserByEmail("tu@example.com");
    }

    @Test
    @DisplayName("Create habit")
    void testCreateHabit() {
        String title = "Do push ups";
        String description = "Do 10 push ups every day";
        Frequency frequency = Frequency.DAILY;
        HabitDTO habitDTO = new HabitDTO(title, description, frequency);

        HabitDTO createdHabit = habitService.createHabit(user.getEmail(), habitDTO);

        assertThat(createdHabit.getTitle()).isEqualTo(title);
        assertThat(createdHabit.getDescription()).isEqualTo(description);
        assertThat(createdHabit.getFrequency()).isEqualTo(frequency);
        assertThat(habitService.getAllHabits(user.getEmail())).containsKey(title);
    }

    @Test
    @DisplayName("Edit habit")
    void testEditHabit() {
        String oldTitle = "Read a book";
        HabitDTO oldHabitDTO = new HabitDTO(oldTitle, "Description", Frequency.DAILY);
        HabitDTO habit = habitService.createHabit(user.getEmail(), oldHabitDTO);
        assertThat(habit.getTitle()).isEqualTo(oldTitle);

        String newTitle = "Read a novel";
        String newDescription = "Read for 1 hour";
        Frequency newFrequency = Frequency.WEEKLY;
        HabitDTO habitDTOToUpdate = new HabitDTO(newTitle, newDescription, newFrequency);

        HabitDTO updatedHabitDTO = habitService.editHabit(user.getEmail(), oldTitle, habitDTOToUpdate);

        assertThat(updatedHabitDTO).isNotNull();
        assertThat(updatedHabitDTO.getTitle()).isEqualTo(newTitle);
        assertThat(updatedHabitDTO.getDescription()).isEqualTo(newDescription);
        assertThat(updatedHabitDTO.getFrequency()).isEqualTo(newFrequency);
    }

    @Test
    @DisplayName("Delete habit")
    void testDeleteHabit() {
        String title = "Drink water";
        HabitDTO habitDTO = habitService.createHabit(user.getEmail(),
                new HabitDTO(title, "Description", Frequency.DAILY));
        assertThat(habitService.getAllHabits(user.getEmail())).containsKey(title);

        habitService.deleteHabit(user.getEmail(), title);
        assertThat(habitService.getAllHabits(user.getEmail())).doesNotContainKey(title);
    }
}
