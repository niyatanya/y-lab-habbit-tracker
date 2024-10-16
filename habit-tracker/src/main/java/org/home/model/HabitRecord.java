package org.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
public class HabitRecord {
    private LocalDate date;
    private boolean completed;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HabitRecord that = (HabitRecord) o;
        return completed == that.completed && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(date, completed);
    }
}
