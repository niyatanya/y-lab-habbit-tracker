package org.home.service;

import org.home.model.Frequency;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HabitService test")
class HabitServiceTest {

    private HabitService habitService;
    private User user;

    @BeforeEach
    void setUp() {
        habitService = new HabitService();
        user = new User("John Doe", "john@example.com", "password123");
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
        assertThat(user.getAllHabits()).contains(createdHabit);
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

        assertThat(user.getAllHabits()).doesNotContain(habit);
    }

    @Test
    @DisplayName("Get all habits")
    void testGetAllHabits() {
        Habit habit1 = habitService.createHabit(user, "Read", "Read daily", Frequency.DAILY);
        Habit habit2 = habitService.createHabit(user, "Exercise", "Exercise weekly", Frequency.WEEKLY);

        List<Habit> habits = habitService.getAllHabits(user);

        assertThat(habits).hasSize(2);
        assertThat(habits).contains(habit1, habit2);
    }

    @Test
    @DisplayName("Track a habit")
    void testTrackHabit() {
        String title = "Read a book";
        LocalDate date = LocalDate.now();
        boolean completed = true;

        Habit habit = habitService.createHabit(user, title, "Description", Frequency.DAILY);

        habitService.trackHabit(user, title, date, completed);

        HabitRecord record = new HabitRecord(date, completed);
        assertThat(habit.getCompletionHistory()).contains(record);
    }

    @Test
    @DisplayName("Find habit by title with existing title")
    void testFindHabitByTitle_Found() {
        String title = "Read a book";
        Habit habit = habitService.createHabit(user, title, "Read daily", Frequency.DAILY);

        Habit foundHabit = habitService.findHabitByTitle(user, title);

        assertThat(foundHabit).isNotNull();
        assertThat(foundHabit.getTitle()).isEqualTo(title);
    }

    @Test
    @DisplayName("Find habit by title with non-existing title")
    void testFindHabitByTitle_NotFound() {
        String title = "Non-existing habit";

        Habit foundHabit = habitService.findHabitByTitle(user, title);

        assertThat(foundHabit).isNull();
    }
}
