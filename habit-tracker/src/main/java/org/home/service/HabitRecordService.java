package org.home.service;

import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.repository.HabitRecordRepository;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HabitRecordService {

    public HabitRecord createRecord(Habit habit, LocalDate date, boolean completed) {
        if (HabitRecordRepository.recordExists(habit.getId(), date)) {
            return null;
        }
        HabitRecord record = new HabitRecord(date, completed, habit.getId());
        HabitRecordRepository.save(record);
        return record;
    }

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

    public void deleteRecord(Habit habit, LocalDate date) {
        Optional<HabitRecord> maybeRecord = HabitRecordRepository.findByDateAndHabitId(date, habit.getId());
        maybeRecord.ifPresent(HabitRecordRepository::delete);
    }

    public Map<LocalDate, HabitRecord> getAllRecords(Habit habit) {
        return new HashMap<>(HabitRecordRepository.getAllHabitRecords(habit));
    }

    public HabitRecord findByDateAndHabitId(Habit habit, LocalDate date) {
        return HabitRecordRepository.findByDateAndHabitId(date, habit.getId()).orElseThrow();
    }

    public boolean recordExists(Long habitId, LocalDate date) {
        return HabitRecordRepository.recordExists(habitId, date);
    }
}
