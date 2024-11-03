package org.home.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for statistics input.
 * Holds information required to generate statistics for a specific habit.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StatisticsInputDTO {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String habitTitle;

    @NotNull
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;
}
