package org.home.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

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

    public void editHabit(String newTitle, String newDescription, Frequency newFrequency) {
        this.title = newTitle;
        this.description = newDescription;
        this.frequency = newFrequency;
    }

    public void trackCompletion(LocalDate date, boolean completed) {
        HabitRecord record = findRecordByDate(date);
        if (record == null) {
            record = new HabitRecord(date, completed);
            habitRecords.add(record);
        } else {
            record.setCompleted(completed);
        }
    }

    private HabitRecord findRecordByDate(LocalDate date) {
        return habitRecords.stream()
                .filter(record -> record.getDate().equals(date))
                .findFirst()
                .orElse(null);
    }

    public List<HabitRecord> getCompletionHistory() {
        return habitRecords;
    }

//    public void editHabit(String title, String newTitle, String newDescription, Frequency newFrequency) {
//        Habit habitToEdit = findHabitByTitle(title);
//        if (habitToEdit != null) {
//            habitToEdit.setTitle(newTitle);
//            habitToEdit.setDescription(newDescription);
//            habitToEdit.setFrequency(newFrequency);
//            System.out.println("Habit updated: " + newTitle);
//        } else {
//            System.out.println("Habit not found: " + title);
//        }
//    }
}
