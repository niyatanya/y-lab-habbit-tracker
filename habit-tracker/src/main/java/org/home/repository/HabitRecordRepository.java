package org.home.repository;

import org.home.model.Habit;
import org.home.model.HabitRecord;
import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

/**
 * The {@code HabitRecordRepository} interface provides methods to manage habit records in the database.
 */
public interface HabitRecordRepository {

    /**
     * Retrieves all habit records associated with a specific habit.
     *
     * @param habit the {@link Habit} for which to retrieve records
     * @return a map of dates to {@link HabitRecord} objects for the specified habit
     */
    Map<LocalDate, HabitRecord> getAllHabitRecords(Habit habit);

    /**
     * Saves a new habit record to the database.
     *
     * @param record the {@link HabitRecord} to be saved
     */
    void save(HabitRecord record);

    /**
     * Updates an existing habit record.
     *
     * @param record the {@link HabitRecord} to update
     * @return {@code true} if the update was successful; {@code false} otherwise
     */
    boolean update(HabitRecord record);

    /**
     * Deletes a habit record from the database.
     *
     * @param record the {@link HabitRecord} to delete
     * @return {@code true} if the deletion was successful; {@code false} otherwise
     */
    boolean delete(HabitRecord record);

    /**
     * Checks if a habit record exists for a specific habit on a given date.
     *
     * @param habitId the ID of the habit
     * @param date    the date to check for the habit record
     * @return {@code true} if the record exists; {@code false} otherwise
     */
    boolean recordExists(Long habitId, LocalDate date);

    /**
     * Finds a habit record by its date and associated habit ID.
     *
     * @param date    the date of the habit record
     * @param habitId the ID of the associated habit
     * @return an {@link Optional} containing the {@link HabitRecord} if found, or an empty {@link Optional}
     */
    Optional<HabitRecord> findByDateAndHabitId(LocalDate date, Long habitId);
}
