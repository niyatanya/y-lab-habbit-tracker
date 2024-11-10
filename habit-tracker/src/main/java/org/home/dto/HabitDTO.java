package org.home.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.home.model.Frequency;

import java.util.Objects;

/**
 * Data Transfer Object (DTO) for a habit.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HabitDTO {

    @Size(min = 2, max = 50)
    private String title;

    @Size(min = 2, max = 500)
    private String description;

    @NotNull
    private Frequency frequency;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        HabitDTO habitDTO = (HabitDTO) o;
        return Objects.equals(title, habitDTO.title)
                && Objects.equals(description, habitDTO.description)
                && frequency == habitDTO.frequency;
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, frequency);
    }
}
