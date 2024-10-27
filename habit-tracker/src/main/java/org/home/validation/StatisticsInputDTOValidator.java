package org.home.validation;

import org.home.dto.StatisticsInputDTO;
import java.time.LocalDate;

public class StatisticsInputDTOValidator {

    public static void validate(StatisticsInputDTO dto) {
        validateEmail(dto.getEmail());
        validateTitle(dto.getHabitTitle());
        validateDates(dto.getStartDate(), dto.getEndDate());
    }

    private static void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private static void validateTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Habit title must not be empty.");
        }
    }

    private static void validateDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Both dates must not be null.");
        }
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date.");
        }
    }
}
