package org.home.repository;

import org.home.model.Habit;
import org.home.model.User;
import java.util.Map;
import java.util.Optional;

/**
 * The {@code HabitRepository} interface provides methods to manage habits in the database.
 */
public interface HabitRepository {

    /**
     * Retrieves all habits associated with a specific user.
     *
     * @param user the {@link User} for whom to retrieve habits
     * @return a map of habit titles to {@link Habit} objects for the specified user
     */
    Map<String, Habit> getAllUserHabits(User user);

    /**
     * Saves a new habit to the database.
     *
     * @param habit the {@link Habit} to be saved
     */
    void save(Habit habit);

    /**
     * Updates an existing habit in the database.
     *
     * @param habit the {@link Habit} to update
     * @return {@code true} if the update was successful; {@code false} otherwise
     */
    boolean update(Habit habit);

    /**
     * Deletes a habit from the database.
     *
     * @param habit the {@link Habit} to delete
     * @return {@code true} if the deletion was successful; {@code false} otherwise
     */
    boolean delete(Habit habit);

    /**
     * Checks if a habit exists for a specific user with the given title.
     *
     * @param userId the ID of the user
     * @param title  the title of the habit to check
     * @return {@code true} if the habit exists; {@code false} otherwise
     */
    boolean habitExists(Long userId, String title);

    /**
     * Finds a habit by its title and associated user ID.
     *
     * @param title  the title of the habit
     * @param userId the ID of the associated user
     * @return an {@link Optional} containing the {@link Habit} if found, or an empty {@link Optional}
     */
    Optional<Habit> findByTitleAndUserId(String title, Long userId);
}
