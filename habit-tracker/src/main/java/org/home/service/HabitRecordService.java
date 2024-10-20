package org.home.service;

import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.repository.HabitRecordRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The {@code HabitRecordService} class provides methods for managing habit records.
 */
public class HabitRecordService {

    /**
     * Creates a new habit record for a specified habit on a given date.
     *
     * @param habit    the {@link Habit} associated with the record
     * @param date     the date of the habit record
     * @param completed whether the habit was completed on that date
     * @return the created {@link HabitRecord}, or {@code null} if a record for that date already exists
     */
    public HabitRecord createRecord(Habit habit, LocalDate date, boolean completed) {
        if (HabitRecordRepository.recordExists(habit.getId(), date)) {
            return null;
        }
        HabitRecord record = new HabitRecord(date, completed, habit.getId());
        HabitRecordRepository.save(record);
        return record;
    }

    /**
     * Edits the completion status of an existing habit record for a specific habit on a given date.
     *
     * @param habit         the {@link Habit} associated with the record
     * @param oldCompleted  the old completion status of the record
     * @param newCompleted  the new completion status to set
     * @param date          the date of the habit record
     */
    public void editRecord(Habit habit, boolean oldCompleted, boolean newCompleted, LocalDate date) {
        if (oldCompleted == newCompleted) {
            return;
        }

        Optional<HabitRecord> maybeRecord = HabitRecordRepository.findByDateAndHabitId(date, habit.getId());
        if (maybeRecord.isPresent()) {
            HabitRecord record = maybeRecord.get();
            record.setCompleted(newCompleted);
            HabitRecordRepository.update(record);
        }
    }

    /**
     * Deletes a habit record for a specific habit on a given date.
     *
     * @param habit the {@link Habit} associated with the record
     * @param date  the date of the habit record to delete
     */
    public void deleteRecord(Habit habit, LocalDate date) {
        Optional<HabitRecord> maybeRecord = HabitRecordRepository.findByDateAndHabitId(date, habit.getId());
        maybeRecord.ifPresent(HabitRecordRepository::delete);
    }

    /**
     * Retrieves all habit records associated with a specified habit.
     *
     * @param habit the {@link Habit} for which to retrieve records
     * @return a map of dates to {@link HabitRecord} objects
     */
    public Map<LocalDate, HabitRecord> getAllRecords(Habit habit) {
        return new HashMap<>(HabitRecordRepository.getAllHabitRecords(habit));
    }

    /**
     * Finds a habit record by date and habit ID.
     *
     * @param habit the {@link Habit} associated with the record
     * @param date  the date of the habit record
     * @return the found {@link HabitRecord}
     */
    public HabitRecord findByDateAndHabitId(Habit habit, LocalDate date) {
        return HabitRecordRepository.findByDateAndHabitId(date, habit.getId()).orElseThrow();
    }

    /**
     * Checks if a record exists for a specific habit on a given date.
     *
     * @param habitId the ID of the habit to check
     * @param date    the date to check for the habit record
     * @return {@code true} if a record exists; {@code false} otherwise
     */
    public boolean recordExists(Long habitId, LocalDate date) {
        return HabitRecordRepository.recordExists(habitId, date);
    }
}
