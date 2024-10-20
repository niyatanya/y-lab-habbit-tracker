package org.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The {@code Habit} class represents a user's habit with a title, description,
 * frequency, and the ID of the user who owns it.
 */
@Getter
@Setter
@AllArgsConstructor
public class Habit {
    private Long id;
    private String title;
    private String description;
    private Frequency frequency;
    private Long userId;

    /**
     * Constructs a new {@code Habit} with the specified title, description, frequency, and user ID.
     *
     * @param title       the title of the habit
     * @param description a brief description of the habit
     * @param frequency   the frequency of the habit
     * @param userId      the ID of the user who owns the habit
     */
    public Habit(String title, String description, Frequency frequency, Long userId) {
        this.title = title;
        this.description = description;
        this.frequency = frequency;
        this.userId = userId;
    }
}
