package org.home.service;

import org.home.dto.HabitRecordDTO;
import org.home.mapper.HabitMapper;
import org.home.mapper.HabitRecordMapper;
import org.home.mapper.UserMapper;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.home.repository.jdbc.JdbcHabitRecordRepository;
import org.home.repository.jdbc.JdbcHabitRepository;
import org.home.repository.jdbc.JdbcUserRepository;
import org.home.service.impl.HabitRecordServiceImpl;
import org.home.service.impl.HabitServiceImpl;
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

@DisplayName("HabitRecordService test")
public class HabitRecordServiceTest {

    private static PostgreSQLContainer<?> testDb = new PostgreSQLContainer<>("postgres")
            .withInitScript("test-schema.sql");

    private static final UserMapper USER_MAPPER = Mappers.getMapper(UserMapper.class);
    private static final HabitMapper HABIT_MAPPER = Mappers.getMapper(HabitMapper.class);
    private static final HabitRecordMapper RECORD_MAPPER = Mappers.getMapper(HabitRecordMapper.class);

    private HabitRecordService recordService;
    private HabitRecordRepository recordRepository;
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
        recordRepository = new JdbcHabitRecordRepository(dataSource);

        UserService userService = new UserServiceImpl(USER_MAPPER, userRepository);
        HabitService habitService = new HabitServiceImpl(HABIT_MAPPER, userRepository, habitRepository);
        recordService = new HabitRecordServiceImpl(RECORD_MAPPER, userRepository, habitRepository, recordRepository);

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
