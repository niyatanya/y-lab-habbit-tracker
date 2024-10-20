package org.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * The {@code HabitRecord} class represents a record of a specific habit on a given date.
 * It tracks whether the habit was completed on that date and is associated with a habit by its ID.
 */
@Getter
@Setter
@AllArgsConstructor
public class HabitRecord {
    private Long id;
    private LocalDate date;
    private boolean completed;
    private Long habitId;

    /**
     * Constructs a new {@code HabitRecord} with the specified date, completion status, and habit ID.
     *
     * @param date      the date on which the habit was tracked
     * @param completed whether the habit was completed on that date
     * @param habitId   the ID of the associated habit
     */
    public HabitRecord(LocalDate date, boolean completed, Long habitId) {
        this.date = date;
        this.completed = completed;
        this.habitId = habitId;
    }
}
