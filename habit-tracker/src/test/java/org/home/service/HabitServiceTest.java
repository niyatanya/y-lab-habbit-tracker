package org.home.service;

import org.home.dto.HabitDTO;
import org.home.mapper.HabitMapper;
import org.home.mapper.UserMapper;
import org.home.model.Frequency;
import org.home.model.User;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HabitService test")
class HabitServiceTest {

    private static PostgreSQLContainer<?> testDb = new PostgreSQLContainer<>("postgres")
            .withInitScript("test-schema.sql");

    private static final UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);
    private static final HabitMapper HABIT_MAPPER = Mappers.getMapper(HabitMapper.class);

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
        DataSource dataSource = new DriverManagerDataSource(
                testDb.getJdbcUrl(),
                testDb.getUsername(),
                testDb.getPassword()
        );
        UserRepository userRepository = new UserRepository(dataSource);
        HabitRepository habitRepository = new HabitRepository(dataSource);

        UserService userService = new UserService(USER_MAPPER, userRepository);
        habitService = new HabitService(HABIT_MAPPER, userRepository, habitRepository);

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
