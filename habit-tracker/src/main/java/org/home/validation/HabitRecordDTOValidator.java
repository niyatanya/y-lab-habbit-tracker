package org.home.validation;

import org.home.dto.HabitRecordDTO;
import java.time.LocalDate;

/**
 * Validator for {@link HabitRecordDTO} that checks the validity of fields.
 */
public class HabitRecordDTOValidator {

    /**
     * Validates the given {@link HabitRecordDTO}.
     *
     * @param dto the {@code HabitRecordDTO} to validate
     * @throws IllegalArgumentException if the date is {@code null} or set in the future
     */
    public static void validate(HabitRecordDTO dto) {
        if (dto.getDate() == null) {
            throw new IllegalArgumentException("Date must not be null.");
        }
        if (dto.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date must not be in the future.");
        }
    }
}
