package org.home.service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.home.model.Frequency;
import org.home.model.Habit;
import org.home.model.User;
import org.home.repository.HabitRepository;

/**
 * The {@code HabitService} class provides methods for managing habits associated with users.
 */
public class HabitService {

    /**
     * Creates a new habit for a specified user.
     *
     * @param user        the {@link User} associated with the habit
     * @param title       the title of the new habit
     * @param description a description of the new habit
     * @param frequency   the {@link Frequency} indicating how often the habit should be performed
     * @return the created {@link Habit}, or {@code null} if a habit with the same title already exists for the user
     */
    public Habit createHabit(User user, String title, String description, Frequency frequency) {
        if (HabitRepository.habitExists(user.getId(), title)) {
            return null;
        }

        Habit habit = new Habit(title, description, frequency, user.getId());
        HabitRepository.save(habit);
        return habit;
    }

    /**
     * Edits an existing habit for a specified user.
     *
     * @param user          the {@link User} associated with the habit
     * @param oldTitle      the current title of the habit
     * @param newTitle      the new title for the habit
     * @param newDescription the new description for the habit
     * @param newFrequency  the new {@link Frequency} for the habit
     */
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

    /**
     * Deletes a habit for a specified user.
     *
     * @param user the {@link User} associated with the habit
     * @param title the title of the habit to delete
     */
    public void deleteHabit(User user, String title) {
        Optional<Habit> maybeHabit = HabitRepository.findByTitleAndUserId(title, user.getId());
        maybeHabit.ifPresent(HabitRepository::delete);
    }

    /**
     * Retrieves all habits associated with a specified user.
     *
     * @param user the {@link User} for whom to retrieve habits
     * @return a map of titles to {@link Habit} objects
     */
    public Map<String, Habit> getAllHabits(User user) {
        return new HashMap<>(HabitRepository.getAllUserHabits(user));
    }

    /**
     * Finds a habit by title and user ID.
     *
     * @param user  the {@link User} associated with the habit
     * @param title the title of the habit to find
     * @return the found {@link Habit}
     */
    public Habit findByTitleAndUserId(User user, String title) {
        return HabitRepository.findByTitleAndUserId(title, user.getId()).orElseThrow();
    }

    /**
     * Checks if a habit exists for a specific user with a given title.
     *
     * @param userId the ID of the user to check
     * @param title  the title of the habit to check
     * @return {@code true} if the habit exists; {@code false} otherwise
     */
    public boolean habitExists(Long userId, String title) {
        return HabitRepository.habitExists(userId, title);
    }
}
