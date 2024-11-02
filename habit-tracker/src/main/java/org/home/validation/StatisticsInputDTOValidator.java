package org.home.validation;

import org.home.dto.StatisticsInputDTO;
import java.time.LocalDate;

/**
 * Validator for {@link StatisticsInputDTO} that checks the validity of fields.
 */
public class StatisticsInputDTOValidator {

    /**
     * Validates the given {@link StatisticsInputDTO}.
     *
     * @param dto the {@code StatisticsInputDTO} to validate
     * @throws IllegalArgumentException if any field does not meet validation criteria
     */
    public static void validate(StatisticsInputDTO dto) {
        validateEmail(dto.getEmail());
        validateTitle(dto.getHabitTitle());
        validateDates(dto.getStartDate(), dto.getEndDate());
    }

    /**
     * Validates the email field.
     *
     * @param email the email to validate
     * @throws IllegalArgumentException if the email is {@code null} or does not match the expected format
     */
    private static void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Validates the habit title field.
     *
     * @param title the habit title to validate
     * @throws IllegalArgumentException if the title is {@code null} or blank
     */
    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Habit title must not be empty.");
        }
    }

    /**
     * Validates the date range fields.
     *
     * @param startDate the start date of the date range
     * @param endDate   the end date of the date range
     * @throws IllegalArgumentException if either date is {@code null} or if the start date is after the end date
     */
    private static void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both dates must not be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
    }
}
