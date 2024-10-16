package org.home.service;

import java.time.LocalDate;

import org.home.model.Frequency;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;

public class HabitService {

    public Habit createHabit(User user, String title, String description, Frequency frequency) {
        Habit habit = new Habit(title, description, frequency);
        user.getHabits().put(habit.getTitle(), habit);
        return habit;
    }

    public void editHabit(User user, String oldTitle, String newTitle, String newDescription, Frequency newFrequency) {
        Habit habit = user.getHabits().get(oldTitle);
        if (habit != null) {
            user.getHabits().remove(oldTitle);
            habit.setTitle(newTitle);
            habit.setDescription(newDescription);
            habit.setFrequency(newFrequency);
            user.getHabits().put(newTitle, habit);
        }
    }

    public void deleteHabit(User user, String title) {
        Habit habitToRemove = user.getHabits().get(title);
        if (habitToRemove != null) {
            user.getHabits().remove(title);
        }
    }

    public void trackHabit(User user, String title, LocalDate date, boolean completed) {
        Habit habit = user.getHabits().get(title);
        if (habit != null) {
            HabitRecord record = habit.getHabitRecords().get(date);
            if (record == null) {
                record = new HabitRecord(date, completed);
                habit.getHabitRecords().put(date, record);
            } else {
                record.setCompleted(completed);
            }
        }
    }
}
