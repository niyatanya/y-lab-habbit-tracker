package org.home.service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.home.annotations.LoggableUserAction;
import org.home.dto.HabitDTO;
import org.home.mapper.HabitMapper;
import org.home.model.Habit;
import org.home.model.User;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.springframework.stereotype.Service;

/**
 * The {@code HabitService} class provides methods for managing habits associated with users.
 */
@LoggableUserAction
@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitMapper habitMapper;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;

    /**
     * Creates a new habit for a specified user.
     *
     * @param email The email of the user creating the habit.
     * @param habitDTO The data transfer object containing habit details.
     * @return The created habit as a DTO, or {@code null} if a habit with the same title already exists.
     */
    public HabitDTO createHabit(String email, HabitDTO habitDTO) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (habitExists(user.getId(), habitDTO.getTitle())) {
            return null;
        }

        Habit habit = habitMapper.toEntity(habitDTO);
        habit.setUserId(user.getId());
        habitRepository.save(habit);
        return habitMapper.toDTO(habit);
    }

    /**
     * Edits an existing habit for a specified user.
     *
     * @param email The email of the user editing the habit.
     * @param oldTitle The current title of the habit.
     * @param habitDTO The data transfer object containing updated habit details.
     * @return The updated habit as a DTO, or {@code null} if a habit with the same title already exists.
     */
    public HabitDTO editHabit(String email, String oldTitle, HabitDTO habitDTO) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (oldTitle.equals(habitDTO.getTitle()) && habitRepository.habitExists(user.getId(), habitDTO.getTitle())) {
            return null;
        }

        Habit habit = habitRepository.findByTitleAndUserId(oldTitle, user.getId()).orElseThrow();
        habit.setTitle(habitDTO.getTitle());
        habit.setDescription(habitDTO.getDescription());
        habit.setFrequency(habitDTO.getFrequency());
        habitRepository.update(habit);
        return habitMapper.toDTO(habit);
    }

    /**
     * Deletes a habit for a specified user.
     *
     * @param email The email of the user deleting the habit.
     * @param title The title of the habit to delete.
     * @return {@code true} if the habit was successfully deleted, {@code false} otherwise.
     */
    public boolean deleteHabit(String email, String title) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Optional<Habit> maybeHabit = habitRepository.findByTitleAndUserId(title, user.getId());
        if (maybeHabit.isPresent()) {
            habitRepository.delete(maybeHabit.get());
            return true;
        }
        return false;
    }

    /**
     * Retrieves all habits associated with a specified user.
     *
     * @param email The email of the user requesting the habits.
     * @return A map of habit titles to habit DTOs associated with the user.
     */
    public Map<String, HabitDTO> getAllHabits(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Map<String, Habit> habitMap = habitRepository.getAllUserHabits(user);
        return habitMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> habitMapper.toDTO(entry.getValue())
                ));
    }

    /**
     * Finds a habit by title and user ID.
     *
     * @param user  the {@link User} associated with the habit
     * @param title the title of the habit to find
     * @return the found {@link Habit}
     */
    public Habit findByTitleAndUserId(User user, String title) {
        return habitRepository.findByTitleAndUserId(title, user.getId()).orElseThrow();
    }

    /**
     * Checks if a habit exists for a specific user with a given title.
     *
     * @param userId the ID of the user to check
     * @param title  the title of the habit to check
     * @return {@code true} if the habit exists; {@code false} otherwise
     */
    public boolean habitExists(Long userId, String title) {
        return habitRepository.habitExists(userId, title);
    }
}
