package org.home.service;

import org.home.model.Frequency;
import org.home.model.Habit;
import org.home.model.Role;
import org.home.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HabitService test")
class HabitServiceTest {

    private HabitService habitService;
    private User user;

    @BeforeEach
    void setUp() {
        habitService = new HabitService();
        Role initialRole = new Role("USER");
        user = new User("John Doe", "john@example.com", "password123", initialRole);
    }

    @Test
    @DisplayName("Create habit")
    void testCreateHabit() {
        String title = "Read a book";
        String description = "Read every day for 30 minutes";
        Frequency frequency = Frequency.DAILY;

        Habit createdHabit = habitService.createHabit(user, title, description, frequency);

        assertThat(createdHabit.getTitle()).isEqualTo(title);
        assertThat(createdHabit.getDescription()).isEqualTo(description);
        assertThat(createdHabit.getFrequency()).isEqualTo(frequency);
        assertThat(user.getHabits()).containsKey(title);
    }

    @Test
    @DisplayName("Edit habit")
    void testEditHabit() {
        String oldTitle = "Read a book";
        String newTitle = "Read a novel";
        String newDescription = "Read for 1 hour";
        Frequency newFrequency = Frequency.WEEKLY;

        Habit habit = habitService.createHabit(user, oldTitle, "Description", Frequency.DAILY);

        habitService.editHabit(user, oldTitle, newTitle, newDescription, newFrequency);

        assertThat(habit.getTitle()).isEqualTo(newTitle);
        assertThat(habit.getDescription()).isEqualTo(newDescription);
        assertThat(habit.getFrequency()).isEqualTo(newFrequency);
    }

    @Test
    @DisplayName("Delete habit")
    void testDeleteHabit() {
        String title = "Read a book";
        Habit habit = habitService.createHabit(user, title, "Description", Frequency.DAILY);

        habitService.deleteHabit(user, title);

        assertThat(user.getHabits()).doesNotContainKey(title);
    }

    @Test
    @DisplayName("Get all habits")
    void testGetAllHabits() {
        Habit habit1 = habitService.createHabit(user, "Read", "Read daily", Frequency.DAILY);
        Habit habit2 = habitService.createHabit(user, "Exercise", "Exercise weekly", Frequency.WEEKLY);

        Map<String, Habit> habits = user.getHabits();

        assertThat(habits).hasSize(2);
        assertThat(habits).containsKeys(habit1.getTitle(), habit2.getTitle());
    }

    @Test
    @DisplayName("Track a habit")
    void testTrackHabit() {
        String title = "Read a book";
        LocalDate date = LocalDate.now();
        boolean completed = true;

        Habit habit = habitService.createHabit(user, title, "Description", Frequency.DAILY);

        habitService.trackHabit(user, title, date, completed);
        assertThat(habit.getHabitRecords()).containsKey(date);
    }
}
