package org.home.validation;

import org.home.dto.HabitDTO;
import org.home.model.Frequency;

public class HabitDTOValidator {

    public static void validate(HabitDTO dto) {
        validateTitle(dto.getTitle());
        validateDescription(dto.getDescription());
        validateFrequency(dto.getFrequency());
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be empty.");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Title must not exceed 100 characters.");
        }
    }

    private static void validateDescription(String description) {
        if (description.length() > 500) {
            throw new IllegalArgumentException("Description must not exceed 500 characters.");
        }
    }

    private static void validateFrequency(Frequency frequency) {
        if (frequency == null) {
            throw new IllegalArgumentException("Frequency must not be null.");
        }
    }
}
