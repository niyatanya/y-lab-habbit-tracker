package org.home.dto;

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
    private String email;
    private String habitTitle;
    private LocalDate startDate;
    private LocalDate endDate;
}
