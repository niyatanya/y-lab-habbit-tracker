package org.home.service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.home.dto.HabitDTO;
import org.home.mapper.HabitMapper;
import org.home.model.Habit;
import org.home.model.User;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.mapstruct.factory.Mappers;

/**
 * The {@code HabitService} class provides methods for managing habits associated with users.
 */
public class HabitService {

    private static final HabitMapper MAPPER = Mappers.getMapper(HabitMapper.class);

    /**
     * Creates a new habit for a specified user.
     *
     * @return the created {@link Habit}, or {@code null} if a habit with the same title already exists for the user
     */
    public HabitDTO createHabit(String email, HabitDTO habitDTO) {
        User user = UserRepository.findByEmail(email).orElseThrow();
        if (habitExists(user.getId(), habitDTO.getTitle())) {
            return null;
        }

        Habit habit = MAPPER.toEntity(habitDTO);
        habit.setUserId(user.getId());
        HabitRepository.save(habit);
        return MAPPER.toDTO(habit);
    }

    /**
     * Edits an existing habit for a specified user.
     *
     * @param oldTitle      the current title of the habit
     */
    public HabitDTO editHabit(String email, String oldTitle, HabitDTO habitDTO) {
        User user = UserRepository.findByEmail(email).orElseThrow();
        if (oldTitle.equals(habitDTO.getTitle()) && HabitRepository.habitExists(user.getId(), habitDTO.getTitle())) {
            return null;
        }

        Habit habit = HabitRepository.findByTitleAndUserId(oldTitle, user.getId()).orElseThrow();
        habit.setTitle(habitDTO.getTitle());
        habit.setDescription(habitDTO.getDescription());
        habit.setFrequency(habitDTO.getFrequency());
        HabitRepository.update(habit);
        return MAPPER.toDTO(habit);
    }

    /**
     * Deletes a habit for a specified user.
     *
     * @param title the title of the habit to delete
     */
    public boolean deleteHabit(String email, String title) {
        User user = UserRepository.findByEmail(email).orElseThrow();
        Optional<Habit> maybeHabit = HabitRepository.findByTitleAndUserId(title, user.getId());
        if (maybeHabit.isPresent()) {
            HabitRepository.delete(maybeHabit.get());
            return true;
        }
        return false;
    }

    /**
     * Retrieves all habits associated with a specified user.
     *
     * @return a map of titles to {@link Habit} objects
     */
    public Map<String, HabitDTO> getAllHabits(String email) {
        User user = UserRepository.findByEmail(email).orElseThrow();
        Map<String, Habit> habitMap = HabitRepository.getAllUserHabits(user);
        return habitMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> MAPPER.toDTO(entry.getValue())
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
