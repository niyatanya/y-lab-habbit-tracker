package org.home.service.impl;

import lombok.RequiredArgsConstructor;
import org.home.logging.annotations.LoggableUserAction;
import org.home.dto.StatisticsInputDTO;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;
import org.home.model.Frequency;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.home.service.StatisticsService;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@LoggableUserAction
@RequiredArgsConstructor
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitRecordRepository recordRepository;

    @Override
    public int getCurrentStreak(User user, String habitTitle) {
        Optional<Habit> maybeHabit = habitRepository.findByTitleAndUserId(habitTitle, user.getId());
        if (maybeHabit.isEmpty()) {
            System.out.println("Habit not found.");
            return 0;
        }

        Habit habit = maybeHabit.get();
        Map<LocalDate, HabitRecord> completions = recordRepository.getAllHabitRecords(habit);
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

    @Override
    public double getSuccessPercentage(User user, String habitTitle, LocalDate startDate, LocalDate endDate) {
        Optional<Habit> maybeHabit = habitRepository.findByTitleAndUserId(habitTitle, user.getId());
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

    @Override
    public Map<String, Map<String, String>> generateProgressReport(StatisticsInputDTO inputDTO) {
        User user = userRepository.findByEmail(inputDTO.getEmail()).orElseThrow();
        String habitTitle = inputDTO.getHabitTitle();
        Optional<Habit> maybeHabit = habitRepository.findByTitleAndUserId(habitTitle, user.getId());
        if (maybeHabit.isEmpty()) {
            return Map.of(
                    String.format("Progress Report for Habit: %s", habitTitle),
                    Map.of("Error: ", "Habit not found")
                    );
        }

        Habit habit = maybeHabit.get();
        List<HabitRecord> completions;
        long totalDays;

        LocalDate startDate = inputDTO.getStartDate();
        LocalDate endDate = inputDTO.getEndDate();

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

        return Map.of(
                String.format("Progress Report for Habit: %s", habitTitle),
                Map.of("Period: ", String.format("%s to %s", startDate, endDate),
                        "Total intervals: ", String.valueOf(totalDays),
                        "Successful intervals: ", String.valueOf(successfulDays),
                        "Success rate: ", String.format("%.2f%%", successRate),
                        "Current streak: ", String.format("%d intervals", currentStreak)
                ));
    }

    private List<HabitRecord> filterCompletionsByDate(Habit habit, LocalDate startDate, LocalDate endDate) {
        return recordRepository.getAllHabitRecords(habit).entrySet().stream()
                .filter(entry -> !entry.getKey().isBefore(startDate)
                        && !entry.getKey().isAfter(endDate)
                        && entry.getValue().isCompleted())
                .map(Map.Entry::getValue)
                .toList();
    }

    private List<HabitRecord> filterCompletionsByWeek(Habit habit, LocalDate startDate, LocalDate endDate) {
        return recordRepository.getAllHabitRecords(habit).entrySet().stream()
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
