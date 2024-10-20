package org.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Habit {
    private Long id;
    private String title;
    private String description;
    private Frequency frequency;
    private Long userId;

    public Habit(String title, String description, Frequency frequency, Long userId) {
        this.title = title;
        this.description = description;
        this.frequency = frequency;
        this.userId = userId;
    }
}
