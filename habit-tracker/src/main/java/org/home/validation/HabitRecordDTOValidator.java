package org.home.validation;

import org.home.dto.HabitRecordDTO;
import java.time.LocalDate;

public class HabitRecordDTOValidator {
    public static void validate(HabitRecordDTO dto) {
        if (dto.getDate() == null) {
            throw new IllegalArgumentException("Date must not be null.");
        }
        if (dto.getDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date must not be in the future.");
        }
    }
}
