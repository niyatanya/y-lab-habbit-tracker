package org.home.validation;

import org.home.dto.HabitDTO;
import org.home.model.Frequency;

/**
 * Validator for {@link HabitDTO} that checks the validity of fields.
 */
public class HabitDTOValidator {

    /**
     * Validates the given {@link HabitDTO}.
     *
     * @param dto the {@code HabitDTO} to validate
     * @throws IllegalArgumentException if any field of the DTO is invalid
     */
    public static void validate(HabitDTO dto) {
        validateTitle(dto.getTitle());
        validateDescription(dto.getDescription());
        validateFrequency(dto.getFrequency());
    }

    /**
     * Validates the title of the habit.
     *
     * @param title the title to validate
     * @throws IllegalArgumentException if the title is empty, blank, or exceeds 100 characters
     */
    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title must not be empty.");
        }
        if (title.length() > 100) {
            throw new IllegalArgumentException("Title must not exceed 100 characters.");
        }
    }

    /**
     * Validates the description of the habit.
     *
     * @param description the description to validate
     * @throws IllegalArgumentException if the description exceeds 500 characters
     */
    private static void validateDescription(String description) {
        if (description.length() > 500) {
            throw new IllegalArgumentException("Description must not exceed 500 characters.");
        }
    }

    /**
     * Validates the frequency of the habit.
     *
     * @param frequency the frequency to validate
     * @throws IllegalArgumentException if the frequency is {@code null}
     */
    private static void validateFrequency(Frequency frequency) {
        if (frequency == null) {
            throw new IllegalArgumentException("Frequency must not be null.");
        }
    }
}
