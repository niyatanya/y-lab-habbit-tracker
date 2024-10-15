package org.home.service;

import java.time.LocalDate;

import org.home.model.Frequency;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;

public class HabitService {

    public Habit createHabit(User user, String title, String description, Frequency frequency) {
        Habit habit = new Habit(title, description, frequency);
        user.getHabits().add(habit);
        return habit;
    }

    public void editHabit(User user, String oldTitle, String newTitle, String newDescription, Frequency newFrequency) {
        Habit habit = findHabitByTitle(user, oldTitle);
        if (habit != null) {
            habit.setTitle(newTitle);
            habit.setDescription(newDescription);
            habit.setFrequency(newFrequency);
        }
    }

    public void deleteHabit(User user, String title) {
        Habit habitToRemove = findHabitByTitle(user, title);
        if (habitToRemove != null) {
            user.getHabits().remove(habitToRemove);
        }
    }

    public void trackHabit(User user, String title, LocalDate date, boolean completed) {
        Habit habit = findHabitByTitle(user, title);
        if (habit != null) {
            HabitRecord record = findRecordByDate(habit, date);
            if (record == null) {
                record = new HabitRecord(date, completed);
                habit.getHabitRecords().add(record);
            } else {
                record.setCompleted(completed);
            }
        }
    }

    Habit findHabitByTitle(User user, String title) {
        return user.getHabits().stream()
                .filter(habit -> habit.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }

    private HabitRecord findRecordByDate(Habit habit, LocalDate date) {
        return habit.getHabitRecords().stream()
                .filter(record -> record.getDate().equals(date))
                .findFirst()
                .orElse(null);
    }
}
