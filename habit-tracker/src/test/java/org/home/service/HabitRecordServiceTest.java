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

@DisplayName("HabitRecordService test")
public class HabitRecordServiceTest {

    private static PostgreSQLContainer<?> testDb = new PostgreSQLContainer<>("postgres")
            .withInitScript("test-schema.sql");

    private UserService userService;
    private HabitService habitService;
    private HabitRecordService recordService;
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
    }

    @Test
    @DisplayName("Create record")
    void testCreateRecord() {
        LocalDate date = LocalDate.of(2024, 10, 1);
        boolean completed = true;

        HabitRecord createdRecord = recordService.createRecord(habit, date, completed);

        assertThat(createdRecord).isNotNull();
        assertThat(createdRecord.getDate()).isEqualTo(date);
        assertThat(createdRecord.isCompleted()).isEqualTo(completed);
        assertThat(recordService.getAllRecords(habit)).containsKey(date);
    }

    @Test
    @DisplayName("Edit record")
    void testEditRecord() {
        LocalDate date = LocalDate.of(2024, 10, 2);
        boolean oldCompleted = true;
        boolean newCompleted = false;

        HabitRecord record = recordService.createRecord(habit, date, oldCompleted);
        assertThat(record.getDate()).isEqualTo(date);

        recordService.editRecord(habit, oldCompleted, newCompleted, date);

        HabitRecord editedRecord = recordService.findByDateAndHabitId(habit, date);

        assertThat(editedRecord).isNotNull();
        assertThat(editedRecord.isCompleted()).isEqualTo(newCompleted);
    }

    @Test
    @DisplayName("Delete record")
    void testDeleteRecord() {
        LocalDate date = LocalDate.of(2024, 10, 3);
        HabitRecord record = recordService.createRecord(habit, date, false);
        assertThat(recordService.getAllRecords(habit)).containsKey(date);

        recordService.deleteRecord(habit, date);
        assertThat(recordService.getAllRecords(habit)).doesNotContainKey(date);
    }
}
