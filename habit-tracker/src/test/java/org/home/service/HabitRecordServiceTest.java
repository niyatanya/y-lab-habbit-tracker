package org.home.service;

import org.home.dto.HabitRecordDTO;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;
import org.home.repository.HabitRecordRepository;
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
@DisplayName("HabitRecordService test")
public class HabitRecordServiceTest {

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
    private HabitRecordService recordService;

    @Autowired
    private HabitRecordRepository recordRepository;

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
    @DisplayName("Create record")
    void testCreateRecord() {
        LocalDate date = LocalDate.of(2024, 10, 1);
        boolean completed = true;
        HabitRecordDTO recordDTO = new HabitRecordDTO(date, completed);

        HabitRecordDTO createdRecordDTO = recordService.createRecord(user.getEmail(), habit.getTitle(), recordDTO);

        assertThat(createdRecordDTO).isNotNull();
        assertThat(createdRecordDTO.getDate()).isEqualTo(date);
        assertThat(createdRecordDTO.isCompleted()).isEqualTo(completed);
        assertThat(recordService.getAllRecords(user.getEmail(), habit.getTitle())).containsKey(date);
    }

    @Test
    @DisplayName("Edit record")
    void testEditRecord() {
        LocalDate date = LocalDate.of(2024, 10, 2);
        boolean oldCompleted = true;
        HabitRecordDTO oldRecordDTO = new HabitRecordDTO(date, oldCompleted);
        HabitRecordDTO recordDTO = recordService.createRecord(user.getEmail(), habit.getTitle(), oldRecordDTO);
        assertThat(recordDTO.getDate()).isEqualTo(date);

        boolean newCompleted = false;
        HabitRecordDTO newRecordDTO = new HabitRecordDTO(date, newCompleted);
        recordService.editRecord(user.getEmail(), habit.getTitle(), newRecordDTO);

        HabitRecord editedRecord = recordRepository.findByDateAndHabitId(date, habit.getId()).orElseThrow();

        assertThat(editedRecord).isNotNull();
        assertThat(editedRecord.isCompleted()).isEqualTo(newCompleted);
    }

    @Test
    @DisplayName("Delete record")
    void testDeleteRecord() {
        LocalDate date = LocalDate.of(2024, 10, 3);
        boolean completed = true;
        HabitRecordDTO recordDTO = new HabitRecordDTO(date, completed);

        HabitRecordDTO recordDTOToDelete = recordService.createRecord(user.getEmail(), habit.getTitle(), recordDTO);
        assertThat(recordService.getAllRecords(user.getEmail(), habit.getTitle())).containsKey(date);

        recordService.deleteRecord(user.getEmail(), habit.getTitle(), recordDTOToDelete);
        assertThat(recordService.getAllRecords(user.getEmail(), habit.getTitle())).doesNotContainKey(date);
    }
}
