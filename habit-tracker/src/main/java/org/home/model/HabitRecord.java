package org.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class HabitRecord {
    private Long id;
    private LocalDate date;
    private boolean completed;
    private Long habitId;

    public HabitRecord(LocalDate date, boolean completed, Long habitId) {
        this.date = date;
        this.completed = completed;
        this.habitId = habitId;
    }
}
