package org.home.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Habit {
    private String title;
    private String description;
    private Frequency frequency;
    private List<HabitRecord> habitRecords = new ArrayList<>();

    public Habit(String title, String description, Frequency frequency) {
        this.title = title;
        this.description = description;
        this.frequency = frequency;
    }
}
