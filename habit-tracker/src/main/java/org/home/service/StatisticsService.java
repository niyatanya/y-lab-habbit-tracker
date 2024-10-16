package org.home.service;

import lombok.NoArgsConstructor;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;
import org.home.model.Frequency;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NoArgsConstructor
public class StatisticsService {
    private final HabitService habitService = new HabitService();

    public int getCurrentStreak(User user, String habitTitle) {
        Habit habit = user.getHabits().get(habitTitle);
        if (habit == null) {
            System.out.println("Habit not found.");
            return 0;
        }

        Map<LocalDate, HabitRecord> completions = habit.getHabitRecords();
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

    public double getSuccessPercentage(User user, String habitTitle, LocalDate startDate, LocalDate endDate) {
        Habit habit = user.getHabits().get(habitTitle);
        if (habit == null) {
            System.out.println("Habit not found.");
            return 0.0;
        }

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

    public String generateProgressReport(User user, String habitTitle, LocalDate startDate, LocalDate endDate) {
        Habit habit = user.getHabits().get(habitTitle);
        if (habit == null) {
            return "Habit not found.";
        }

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
        return habit.getHabitRecords().entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startDate)
                        && !entry.getKey().isAfter(endDate)
                        && entry.getValue().isCompleted())
                .map(Map.Entry::getValue)
                .toList();
    }

    private List<HabitRecord> filterCompletionsByWeek(Habit habit, LocalDate startDate, LocalDate endDate) {
        return habit.getHabitRecords().entrySet().stream()
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
