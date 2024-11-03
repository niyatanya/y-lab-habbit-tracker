package org.home.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.home.model.Frequency;

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
}
