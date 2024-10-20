package org.home.service;

import org.home.config.DBConnectionProvider;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("StatisticsService test")
public class StatisticsServiceTest {
    private static PostgreSQLContainer<?> testDb = new PostgreSQLContainer<>("postgres")
            .withInitScript("test-schema.sql");

    private UserService userService;
    private HabitService habitService;
    private HabitRecordService recordService;
    private StatisticsService statisticsService;
    private User user;
    private Habit habit;
    private HabitRecord habitRecord1;
    private HabitRecord habitRecord2;

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
        habit = habitService.findByTitleAndUserId(user, "Go to shower");

        recordService = new HabitRecordService();
        HabitRecordRepository recordRepository = new HabitRecordRepository(connectionProvider);
        habitRecord1 = recordService.findByDateAndHabitId(habit, LocalDate.parse("2024-10-19"));
        habitRecord2 = recordService.findByDateAndHabitId(habit, LocalDate.parse("2024-10-20"));

        statisticsService = new StatisticsService();
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
