package org.home.service.impl;

import lombok.RequiredArgsConstructor;
import org.home.logging.annotations.LoggableUserAction;
import org.home.dto.HabitRecordDTO;
import org.home.mapper.HabitRecordMapper;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.model.User;
import org.home.repository.HabitRecordRepository;
import org.home.repository.HabitRepository;
import org.home.repository.UserRepository;
import org.home.service.HabitRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@LoggableUserAction
@Service
@RequiredArgsConstructor
public class HabitRecordServiceImpl implements HabitRecordService {

    private final HabitRecordMapper recordMapper;
    private final UserRepository userRepository;
    private final HabitRepository habitRepository;
    private final HabitRecordRepository recordRepository;

    @Override
    public HabitRecordDTO createRecord(String email, String habitTitle, HabitRecordDTO recordDTO) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Habit habit = habitRepository.findByTitleAndUserId(habitTitle, user.getId()).orElseThrow();
        if (recordRepository.recordExists(habit.getId(), recordDTO.getDate())) {
            return null;
        }
        HabitRecord record = recordMapper.toEntity(recordDTO);
        record.setHabitId(habit.getId());
        recordRepository.save(record);
        return recordMapper.toDTO(record);
    }

    @Override
    public HabitRecordDTO editRecord(String email, String habitTitle, HabitRecordDTO recordDTO) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Habit habit = habitRepository.findByTitleAndUserId(habitTitle, user.getId()).orElseThrow();
        HabitRecord record = recordRepository.findByDateAndHabitId(
                recordDTO.getDate(), habit.getId()).orElseThrow();
        if (record.isCompleted() == recordDTO.isCompleted()) {
            return null;
        }

        record.setCompleted(recordDTO.isCompleted());
        recordRepository.update(record);
        return recordMapper.toDTO(record);
    }

    @Override
    public boolean deleteRecord(String email, String habitTitle, HabitRecordDTO recordDTO) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Habit habit = habitRepository.findByTitleAndUserId(habitTitle, user.getId()).orElseThrow();
        Optional<HabitRecord> maybeRecord = recordRepository.findByDateAndHabitId(
                recordDTO.getDate(), habit.getId());
        if (maybeRecord.isPresent()) {
            recordRepository.delete(maybeRecord.get());
            return true;
        }
        return false;
    }

    @Override
    public Map<LocalDate, HabitRecordDTO> getAllRecords(String email, String habitTitle) {
        User user = userRepository.findByEmail(email).orElseThrow();
        Habit habit = habitRepository.findByTitleAndUserId(habitTitle, user.getId()).orElseThrow();
        Map<LocalDate, HabitRecord> recordMap = recordRepository.getAllHabitRecords(habit);
        return recordMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> recordMapper.toDTO(entry.getValue())
                ));
    }
}
