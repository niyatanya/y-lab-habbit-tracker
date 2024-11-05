package org.home.service;

import lombok.RequiredArgsConstructor;
import org.home.annotations.LoggableUserAction;
import org.home.dto.HabitRecordDTO;
import org.home.mapper.HabitRecordMapper;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The {@code HabitRecordService} class provides methods for managing habit records.
 */
@LoggableUserAction
@Service
@RequiredArgsConstructor
public class HabitRecordService {

    private final HabitRecordMapper recordMapper;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitRecordRepository recordRepository;

    /**
     * Creates a new habit record for a specified habit on a given date.
     *
     * @param email The email of the user creating the record.
     * @param habitTitle The title of the habit for which the record is created.
     * @param recordDTO The data transfer object containing record details.
     * @return The created habit record as a DTO, or {@code null} if a record already exists for the date.
     */
    public HabitRecordDTO createRecord(String email, String habitTitle, HabitRecordDTO recordDTO) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Habit habit = habitRepository.findByTitleAndUserId(habitTitle, user.getId()).orElseThrow();
        if (recordExists(habit.getId(), recordDTO.getDate())) {
            return null;
        }
        HabitRecord record = recordMapper.toEntity(recordDTO);
        record.setHabitId(habit.getId());
        recordRepository.save(record);
        return recordMapper.toDTO(record);
    }

    /**
     * Edits the completion status of an existing habit record for a specific habit on a given date.
     *
     * @param email The email of the user editing the record.
     * @param habitTitle The title of the habit for which the record is edited.
     * @param recordDTO The data transfer object containing updated record details.
     * @return The updated habit record as a DTO, or {@code null} if the completion status did not change.
     */
    public HabitRecordDTO editRecord(String email, String habitTitle, HabitRecordDTO recordDTO) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Habit habit = habitRepository.findByTitleAndUserId(habitTitle, user.getId()).orElseThrow();
        HabitRecord record = recordRepository.findByDateAndHabitId(
                recordDTO.getDate(), habit.getId()).orElseThrow();
        if (record.isCompleted() == recordDTO.isCompleted()) {
            return null;
        }

        record.setCompleted(recordDTO.isCompleted());
        recordRepository.update(record);
        return recordMapper.toDTO(record);
    }

    /**
     * Deletes a habit record for a specific habit on a given date.
     *
     * @param email The email of the user deleting the record.
     * @param habitTitle The title of the habit for which the record is deleted.
     * @param recordDTO The data transfer object containing the date of the record to be deleted.
     * @return {@code true} if the record was successfully deleted, {@code false} otherwise.
     */
    public boolean deleteRecord(String email, String habitTitle, HabitRecordDTO recordDTO) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Habit habit = habitRepository.findByTitleAndUserId(habitTitle, user.getId()).orElseThrow();
        Optional<HabitRecord> maybeRecord = recordRepository.findByDateAndHabitId(
                recordDTO.getDate(), habit.getId());
        if (maybeRecord.isPresent()) {
            recordRepository.delete(maybeRecord.get());
            return true;
        }
        return false;
    }

    /**
     * Retrieves all habit records associated with a specified habit.
     *
     * @param email The email of the user requesting the records.
     * @param habitTitle The title of the habit for which records are retrieved.
     * @return A map of dates to habit records as DTOs associated with the specified habit.
     */
    public Map<LocalDate, HabitRecordDTO> getAllRecords(String email, String habitTitle) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Habit habit = habitRepository.findByTitleAndUserId(habitTitle, user.getId()).orElseThrow();
        Map<LocalDate, HabitRecord> recordMap = recordRepository.getAllHabitRecords(habit);
        return recordMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> recordMapper.toDTO(entry.getValue())
                ));
    }

    /**
     * Finds a habit record by date and habit ID.
     *
     * @param habit the {@link Habit} associated with the record
     * @param date  the date of the habit record
     * @return the found {@link HabitRecord}
     */
    public HabitRecord findByDateAndHabitId(Habit habit, LocalDate date) {
        return recordRepository.findByDateAndHabitId(date, habit.getId()).orElseThrow();
    }

    /**
     * Checks if a record exists for a specific habit on a given date.
     *
     * @param habitId the ID of the habit to check
     * @param date    the date to check for the habit record
     * @return {@code true} if a record exists; {@code false} otherwise
     */
    public boolean recordExists(Long habitId, LocalDate date) {
        return recordRepository.recordExists(habitId, date);
    }
}
