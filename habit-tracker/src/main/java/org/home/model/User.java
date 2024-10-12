package org.home.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class User {
    private String name;
    private String email;
    private String password;
    private List<Habit> habits = new ArrayList<>();

    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public void addHabit(Habit habit) {
        habits.add(habit);
    }

    public void removeHabit(String title) {
        Habit habitToRemove = findHabitByTitle(title);
        if (habitToRemove != null) {
            habits.remove(habitToRemove);
        }
    }

    public List<Habit> getAllHabits() {
        return habits;
    }

    private Habit findHabitByTitle(String title) {
        return habits.stream()
                .filter(habit -> habit.getTitle().equalsIgnoreCase(title))
                .findFirst()
                .orElse(null);
    }
}
