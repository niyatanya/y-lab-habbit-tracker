package org.home.service;

import lombok.NoArgsConstructor;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;
import org.home.model.Frequency;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class StatisticsService {
    private final HabitService habitService = new HabitService();

    public int getCurrentStreak(User user, String habitTitle) {
        Habit habit = habitService.findHabitByTitle(user, habitTitle);
        if (habit == null) {
            System.out.println("Habit not found.");
            return 0;
        }

        List<HabitRecord> completions = habit.getCompletionHistory();
        if (completions.isEmpty()) {
            return 0;
        }

        LocalDate currentDate = LocalDate.now();
        int streak = 0;


        if (habit.getFrequency() == Frequency.DAILY) {
            for (HabitRecord record : completions) {
                if (record.isCompleted()) {
                    streak++;
                    currentDate = currentDate.minusDays(1);
                }
            }
        } else if (habit.getFrequency() == Frequency.WEEKLY) {
            for (HabitRecord record : completions) {
                if (record.isCompleted()) {
                    streak++;
                    currentDate = currentDate.minusWeeks(1);
                }
            }
        }
        return streak;
    }

    public double getSuccessPercentage(User user, String habitTitle, LocalDate startDate, LocalDate endDate) {
        Habit habit = habitService.findHabitByTitle(user, habitTitle);
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

    public void generateProgressReport(User user, String habitTitle, LocalDate startDate, LocalDate endDate) {
        Habit habit = habitService.findHabitByTitle(user, habitTitle);
        if (habit == null) {
            System.out.println("Habit not found.");
            return;
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

        System.out.printf("Progress Report for Habit: %s%n" +
                        "Period: %s to %s%n" +
                        "Total intervals: %d%n" +
                        "Successful intervals: %d%n" +
                        "Success rate: %.2f%%%n" +
                        "Current streak: %d intervals",
                habitTitle, startDate, endDate, totalDays, successfulDays, successRate, currentStreak);
    }

    private List<HabitRecord> filterCompletionsByDate(Habit habit, LocalDate startDate, LocalDate endDate) {
        return   habit.getCompletionHistory().stream()
                .filter(record -> record.isCompleted()
                        && !record.getDate().isBefore(startDate) && !record.getDate().isAfter(endDate))
                .toList();
    }

    private List<HabitRecord> filterCompletionsByWeek(Habit habit, LocalDate startDate, LocalDate endDate) {
        return habit.getCompletionHistory().stream()
                .filter(record -> {
                    LocalDate startOfWeek = startDate.with(ChronoUnit.DAYS.addTo(startDate, -startDate.getDayOfWeek().getValue() + 1));
                    LocalDate endOfWeek = endDate.with(ChronoUnit.DAYS.addTo(endDate, 7 - endDate.getDayOfWeek().getValue()));
                    return !record.getDate().isBefore(startOfWeek) && !record.getDate().isAfter(endOfWeek);
                })
                .collect(Collectors.toList());
    }
}
