package org.home.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.home.model.Frequency;
import org.home.model.Habit;
import org.home.model.User;
import org.home.repository.HabitRepository;

public class HabitService {

    public Habit createHabit(User user, String title, String description, Frequency frequency) {
        if (HabitRepository.habitExists(user.getId(), title)) {
            return null;
        }

        Habit habit = new Habit(title, description, frequency, user.getId());
        HabitRepository.save(habit);
        return habit;
    }

    public void editHabit(User user, String oldTitle, String newTitle, String newDescription, Frequency newFrequency) {
        Optional<Habit> maybeHabit = HabitRepository.findByTitleAndUserId(oldTitle, user.getId());
        if (maybeHabit.isPresent()) {
            Habit habit = maybeHabit.get();
            habit.setTitle(newTitle);
            habit.setDescription(newDescription);
            habit.setFrequency(newFrequency);
            HabitRepository.update(habit);
        }
    }

    public void deleteHabit(User user, String title) {
        Optional<Habit> maybeHabit = HabitRepository.findByTitleAndUserId(title, user.getId());
        maybeHabit.ifPresent(HabitRepository::delete);
    }

    public Map<String, Habit> getAllHabits(User user) {
        return new HashMap<>(HabitRepository.getAllUserHabits(user));
    }

    public Habit findByTitleAndUserId(User user, String title) {
        return HabitRepository.findByTitleAndUserId(title, user.getId()).orElseThrow();
    }

    public boolean habitExists(Long userId, String title) {
        return HabitRepository.habitExists(userId, title);
    }
}
