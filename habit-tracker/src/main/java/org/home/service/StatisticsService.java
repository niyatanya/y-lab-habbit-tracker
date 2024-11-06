package org.home.service;

import org.home.dto.StatisticsInputDTO;
import org.home.model.User;
import java.time.LocalDate;
import java.util.Map;

/**
 * The {@code StatisticsService} interface provides methods to calculate statistics related to user habits.
 */
public interface StatisticsService {

    /**
     * Calculates the current streak of habit completions for a given user and habit title.
     *
     * @param user       the {@link User} whose habit streak is to be calculated
     * @param habitTitle the title of the habit
     * @return the current streak count; returns 0 if the habit is not found or if there are no completions
     */
    int getCurrentStreak(User user, String habitTitle);

    /**
     * Calculates the success percentage of habit completions for a given user
     * and habit title within a specified date range.
     *
     * @param user       the {@link User} associated with the habit
     * @param habitTitle the title of the habit
     * @param startDate  the start date of the interval
     * @param endDate    the end date of the interval
     * @return the success percentage of habit completions; returns 0.0 if the habit is not found
     * or if the total days is less than or equal to zero
     */
    double getSuccessPercentage(User user, String habitTitle, LocalDate startDate, LocalDate endDate);

    /**
     * Generates a progress report for a given user and habit title within a specified date range.
     *
     * @param inputDTO The data transfer object containing input data for statistics calculation
     * @return a {@link String} containing the result of the operation
     */
    Map<String, Map<String, String>> generateProgressReport(StatisticsInputDTO inputDTO);
}
