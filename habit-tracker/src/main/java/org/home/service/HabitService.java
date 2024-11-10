package org.home.service;

import org.home.dto.HabitDTO;
import java.util.Map;

/**
 * The {@code HabitService} interface provides methods for managing habits associated with users.
 */
public interface HabitService {

    /**
     * Creates a new habit for a specified user.
     *
     * @param email The email of the user creating the habit.
     * @param habitDTO The data transfer object containing habit details.
     * @return The created habit as a DTO, or {@code null} if a habit with the same title already exists.
     */
    HabitDTO createHabit(String email, HabitDTO habitDTO);

    /**
     * Edits an existing habit for a specified user.
     *
     * @param email The email of the user editing the habit.
     * @param oldTitle The current title of the habit.
     * @param habitDTO The data transfer object containing updated habit details.
     * @return The updated habit as a DTO, or {@code null} if a habit with the same title already exists.
     */
    HabitDTO editHabit(String email, String oldTitle, HabitDTO habitDTO);

    /**
     * Deletes a habit for a specified user.
     *
     * @param email The email of the user deleting the habit.
     * @param title The title of the habit to delete.
     * @return {@code true} if the habit was successfully deleted, {@code false} otherwise.
     */
    boolean deleteHabit(String email, String title);

    /**
     * Retrieves all habits associated with a specified user.
     *
     * @param email The email of the user requesting the habits.
     * @return A map of habit titles to habit DTOs associated with the user.
     */
    Map<String, HabitDTO> getAllHabits(String email);
}
