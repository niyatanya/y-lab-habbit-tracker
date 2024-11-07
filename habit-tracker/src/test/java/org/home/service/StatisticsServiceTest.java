package org.home.service;

import org.home.model.Habit;
import org.home.model.User;
import org.home.repository.HabitRepository;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ImportAutoConfiguration(exclude = JdbcRepositoriesAutoConfiguration.class)
@DisplayName("StatisticsService test")
public class StatisticsServiceTest {

    @Container
    private static PostgreSQLContainer<?> testDb = new PostgreSQLContainer<>("postgres")
            .withDatabaseName("test-db")
            .withUsername("test-db-username")
            .withPassword("test-db-pass")
            .withInitScript("test-schema.sql");

    @Autowired
    private HabitRepository habitRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private StatisticsService statisticsService;

    private User user;
    private Habit habit;

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", testDb::getJdbcUrl);
        registry.add("spring.datasource.username", testDb::getUsername);
        registry.add("spring.datasource.password", testDb::getPassword);
    }

    @BeforeEach
    void setUp() {
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
