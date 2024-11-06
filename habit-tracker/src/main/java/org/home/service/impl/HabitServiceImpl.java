package org.home.service.impl;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.home.annotations.LoggableUserAction;
import org.home.dto.HabitDTO;
import org.home.mapper.HabitMapper;
import org.home.model.Habit;
import org.home.model.User;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.home.service.HabitService;
import org.springframework.stereotype.Service;

@LoggableUserAction
@Service
@RequiredArgsConstructor
public class HabitServiceImpl implements HabitService {

    private final HabitMapper habitMapper;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;

    @Override
    public HabitDTO createHabit(String email, HabitDTO habitDTO) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (habitRepository.habitExists(user.getId(), habitDTO.getTitle())) {
            return null;
        }

        Habit habit = habitMapper.toEntity(habitDTO);
        habit.setUserId(user.getId());
        habitRepository.save(habit);
        return habitMapper.toDTO(habit);
    }

    @Override
    public HabitDTO editHabit(String email, String oldTitle, HabitDTO habitDTO) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (oldTitle.equals(habitDTO.getTitle()) && habitRepository.habitExists(user.getId(), habitDTO.getTitle())) {
            return null;
        }

        Habit habit = habitRepository.findByTitleAndUserId(oldTitle, user.getId()).orElseThrow();
        habit.setTitle(habitDTO.getTitle());
        habit.setDescription(habitDTO.getDescription());
        habit.setFrequency(habitDTO.getFrequency());
        habitRepository.update(habit);
        return habitMapper.toDTO(habit);
    }

    @Override
    public boolean deleteHabit(String email, String title) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Optional<Habit> maybeHabit = habitRepository.findByTitleAndUserId(title, user.getId());
        if (maybeHabit.isPresent()) {
            habitRepository.delete(maybeHabit.get());
            return true;
        }
        return false;
    }

    @Override
    public Map<String, HabitDTO> getAllHabits(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Map<String, Habit> habitMap = habitRepository.getAllUserHabits(user);
        return habitMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> habitMapper.toDTO(entry.getValue())
                ));
    }
}
