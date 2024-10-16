package org.home.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Habit {
    private String title;
    private String description;
    private Frequency frequency;
    private Map<LocalDate, HabitRecord> habitRecords = new HashMap<>();

    public Habit(String title, String description, Frequency frequency) {
        this.title = title;
        this.description = description;
        this.frequency = frequency;
    }
}
