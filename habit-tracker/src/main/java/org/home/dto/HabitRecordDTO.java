package org.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Data Transfer Object (DTO) for a habit record.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HabitRecordDTO {
    private LocalDate date;
    private boolean completed;
}
