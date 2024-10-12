package org.home.service;

import java.util.List;
import java.time.LocalDate;

import org.home.model.Frequency;
import org.home.model.Habit;
import org.home.model.User;

public class HabitService {
    public Habit createHabit(User user, String title, String description, Frequency frequency) {
        Habit habit = new Habit(title, description, frequency);
        user.addHabit(habit);
        return habit;
    }

    public void editHabit(User user, String oldTitle, String newTitle, String newDescription, Frequency newFrequency) {
        Habit habit = findHabitByTitle(user, oldTitle);
        if (habit != null) {
            habit.editHabit(newTitle, newDescription, newFrequency);
        }
    }

    public void deleteHabit(User user, String title) {
        Habit habit = findHabitByTitle(user, title);
        if (habit != null) {
            user.removeHabit(title);
        }
    }

    public List<Habit> getAllHabits(User user) {
        return user.getAllHabits();
    }

    public void trackHabit(User user, String title, LocalDate date, boolean completed) {
        Habit habit = findHabitByTitle(user, title);
        if (habit != null) {
            habit.trackCompletion(date, completed);
        }
    }
    Habit findHabitByTitle(User user, String title) {
        return user.getAllHabits().stream()
                .filter(habit -> habit.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }
}
