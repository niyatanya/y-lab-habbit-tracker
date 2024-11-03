package org.home.service;

import org.home.mapper.HabitMapper;
import org.home.mapper.HabitRecordMapper;
import org.home.mapper.UserMapper;
import org.home.model.Habit;
import org.home.model.User;
import org.home.repository.HabitRecordRepository;
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
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StatisticsService test")
public class StatisticsServiceTest {
    private static PostgreSQLContainer<?> testDb = new PostgreSQLContainer<>("postgres")
            .withInitScript("test-schema.sql");

    private static final UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);
    private static final HabitMapper HABIT_MAPPER = Mappers.getMapper(HabitMapper.class);
    private static final HabitRecordMapper RECORD_MAPPER = Mappers.getMapper(HabitRecordMapper.class);

    private StatisticsService statisticsService;
    private User user;
    private Habit habit;

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
        HabitRecordRepository recordRepository = new HabitRecordRepository(dataSource);

        UserService userService = new UserService(USER_MAPPER, userRepository);
        HabitService habitService = new HabitService(HABIT_MAPPER, userRepository, habitRepository);
        HabitRecordService recordService = new HabitRecordService(
                RECORD_MAPPER, userRepository, habitRepository, recordRepository);
        statisticsService = new StatisticsService(userRepository, habitRepository, recordRepository);

        user = userService.findUserByEmail("tu@example.com");
        habit = habitService.findByTitleAndUserId(user, "Go to shower");
    }

    @Test
    @DisplayName("Get current streak")
    void testGetCurrentStreakTest() {
        int streak = statisticsService.getCurrentStreak(user, habit.getTitle());
        assertThat(streak).isEqualTo(2);
    }

    @Test
    @DisplayName("Get success percentage")
    void testGetSuccessPercentageTest() {
        double successPercentage = statisticsService.getSuccessPercentage(
                user,
                habit.getTitle(),
                LocalDate.parse("2024-10-19"),
                LocalDate.parse("2024-10-22")
        );
        assertThat(successPercentage).isEqualTo(50.0);
    }
}
