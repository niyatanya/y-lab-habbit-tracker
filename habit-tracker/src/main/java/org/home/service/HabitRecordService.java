package org.home.service;

import org.home.dto.HabitRecordDTO;
import java.time.LocalDate;
import java.util.Map;

/**
 * The {@code HabitRecordService} interface provides methods for managing habit records.
 */
public interface HabitRecordService {

    /**
     * Creates a new habit record for a specified habit on a given date.
     *
     * @param email The email of the user creating the record.
     * @param habitTitle The title of the habit for which the record is created.
     * @param recordDTO The data transfer object containing record details.
     * @return The created habit record as a DTO, or {@code null} if a record already exists for the date.
     */
    HabitRecordDTO createRecord(String email, String habitTitle, HabitRecordDTO recordDTO);

    /**
     * Edits the completion status of an existing habit record for a specific habit on a given date.
     *
     * @param email The email of the user editing the record.
     * @param habitTitle The title of the habit for which the record is edited.
     * @param recordDTO The data transfer object containing updated record details.
     * @return The updated habit record as a DTO, or {@code null} if the completion status did not change.
     */
    HabitRecordDTO editRecord(String email, String habitTitle, HabitRecordDTO recordDTO);

    /**
     * Deletes a habit record for a specific habit on a given date.
     *
     * @param email The email of the user deleting the record.
     * @param habitTitle The title of the habit for which the record is deleted.
     * @param recordDTO The data transfer object containing the date of the record to be deleted.
     * @return {@code true} if the record was successfully deleted, {@code false} otherwise.
     */
    boolean deleteRecord(String email, String habitTitle, HabitRecordDTO recordDTO);

    /**
     * Retrieves all habit records associated with a specified habit.
     *
     * @param email The email of the user requesting the records.
     * @param habitTitle The title of the habit for which records are retrieved.
     * @return A map of dates to habit records as DTOs associated with the specified habit.
     */
    Map<LocalDate, HabitRecordDTO> getAllRecords(String email, String habitTitle);
}
