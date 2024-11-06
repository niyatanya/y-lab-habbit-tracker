package org.home.service;

import org.home.mapper.HabitMapper;
import org.home.mapper.HabitRecordMapper;
import org.home.mapper.UserMapper;
import org.home.model.Habit;
import org.home.model.User;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.home.repository.jdbc.JdbcHabitRecordRepository;
import org.home.repository.jdbc.JdbcHabitRepository;
import org.home.repository.jdbc.JdbcUserRepository;
import org.home.service.impl.HabitRecordServiceImpl;
import org.home.service.impl.HabitServiceImpl;
import org.home.service.impl.StatisticsServiceImpl;
import org.home.service.impl.UserServiceImpl;
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
        UserRepository userRepository = new JdbcUserRepository(dataSource);
        HabitRepository habitRepository = new JdbcHabitRepository(dataSource);
        HabitRecordRepository recordRepository = new JdbcHabitRecordRepository(dataSource);

        UserService userService = new UserServiceImpl(USER_MAPPER, userRepository);
        HabitService habitService = new HabitServiceImpl(HABIT_MAPPER, userRepository, habitRepository);
        HabitRecordService recordService = new HabitRecordServiceImpl(
                RECORD_MAPPER, userRepository, habitRepository, recordRepository);
        statisticsService = new StatisticsServiceImpl(userRepository, habitRepository, recordRepository);

        user = userService.findUserByEmail("tu@example.com");
        habit = habitRepository.findByTitleAndUserId("Go to shower", user.getId()).orElseThrow();
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
