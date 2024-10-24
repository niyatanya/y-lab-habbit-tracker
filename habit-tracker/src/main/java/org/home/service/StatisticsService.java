package org.home.service;

import lombok.NoArgsConstructor;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;
import org.home.model.Frequency;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The {@code StatisticsService} class provides methods to calculate statistics related to user habits.
 */
@NoArgsConstructor
public class StatisticsService {
    private final HabitService habitService = new HabitService();

    /**
     * Calculates the current streak of habit completions for a given user and habit title.
     *
     * @param user       the {@link User} whose habit streak is to be calculated
     * @param habitTitle the title of the habit
     * @return the current streak count; returns 0 if the habit is not found or if there are no completions
     */
    public int getCurrentStreak(User user, String habitTitle) {
        Optional<Habit> maybeHabit = HabitRepository.findByTitleAndUserId(habitTitle, user.getId());
        if (maybeHabit.isEmpty()) {
            System.out.println("Habit not found.");
            return 0;
        }

        Habit habit = maybeHabit.get();
        Map<LocalDate, HabitRecord> completions = HabitRecordRepository.getAllHabitRecords(habit);
        if (completions.isEmpty()) {
            return 0;
        }

        LocalDate currentDate = LocalDate.now();
        int streak = 0;

        if (habit.getFrequency() == Frequency.DAILY) {
            for (HabitRecord record : completions.values()) {
                if (record.isCompleted()) {
                    streak++;
                    currentDate = currentDate.minusDays(1);
                }
            }
        } else if (habit.getFrequency() == Frequency.WEEKLY) {
            for (HabitRecord record : completions.values()) {
                if (record.isCompleted()) {
                    streak++;
                    currentDate = currentDate.minusWeeks(1);
                }
            }
        }
        return streak;
    }

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
    public double getSuccessPercentage(User user, String habitTitle, LocalDate startDate, LocalDate endDate) {
        Optional<Habit> maybeHabit = HabitRepository.findByTitleAndUserId(habitTitle, user.getId());
        if (maybeHabit.isEmpty()) {
            System.out.println("Habit not found.");
            return 0.0;
        }

        Habit habit = maybeHabit.get();
        List<HabitRecord> completions;
        long totalDays;

        if (habit.getFrequency() == Frequency.DAILY) {
            completions = filterCompletionsByDate(habit, startDate, endDate);
            totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        } else {
            completions = filterCompletionsByWeek(habit, startDate, endDate);
            totalDays = ChronoUnit.WEEKS.between(startDate, endDate) + 1;
        }

        if (totalDays <= 0) {
            return 0.0;
        }

        return (double) completions.size() / totalDays * 100;
    }

    /**
     * Generates a progress report for a given user and habit title within a specified date range.
     *
     * @param user       the {@link User} associated with the habit
     * @param habitTitle the title of the habit
     * @param startDate  the start date of the report period
     * @param endDate    the end date of the report period
     * @return a {@link String} containing the result of the operation
     */
    public String generateProgressReport(User user, String habitTitle, LocalDate startDate, LocalDate endDate) {
        Optional<Habit> maybeHabit = HabitRepository.findByTitleAndUserId(habitTitle, user.getId());
        if (maybeHabit.isEmpty()) {
            return "Habit not found.";
        }

        Habit habit = maybeHabit.get();
        List<HabitRecord> completions;
        long totalDays;

        if (habit.getFrequency() == Frequency.DAILY) {
            completions = filterCompletionsByDate(habit, startDate, endDate);
            totalDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        } else {
            completions = filterCompletionsByWeek(habit, startDate, endDate);
            totalDays = ChronoUnit.WEEKS.between(startDate, endDate) + 1;
        }

        int successfulDays = completions.size();
        double successRate = (double) successfulDays / totalDays * 100;
        int currentStreak = getCurrentStreak(user, habitTitle);

        return String.format("Progress Report for Habit: %s%n"
                        + "Period: %s to %s%n"
                        + "Total intervals: %d%n"
                        + "Successful intervals: %d%n"
                        + "Success rate: %.2f%%%n"
                        + "Current streak: %d intervals",
                habitTitle, startDate, endDate, totalDays, successfulDays, successRate, currentStreak);
    }

    private List<HabitRecord> filterCompletionsByDate(Habit habit, LocalDate startDate, LocalDate endDate) {
        return HabitRecordRepository.getAllHabitRecords(habit).entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startDate)
                        && !entry.getKey().isAfter(endDate)
                        && entry.getValue().isCompleted())
                .map(Map.Entry::getValue)
                .toList();
    }

    private List<HabitRecord> filterCompletionsByWeek(Habit habit, LocalDate startDate, LocalDate endDate) {
        return HabitRecordRepository.getAllHabitRecords(habit).entrySet().stream()
                .filter(entry -> {
                    LocalDate date = entry.getKey();
                    LocalDate startOfWeek = startDate.with(DayOfWeek.MONDAY);
                    LocalDate endOfWeek = endDate.with(DayOfWeek.SUNDAY);
                    return !date.isBefore(startOfWeek)
                            && !date.isAfter(endOfWeek)
                            && entry.getValue().isCompleted();
                })
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());
    }
}
