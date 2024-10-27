package org.home.mapper;

import org.home.dto.HabitDTO;
import org.home.model.Habit;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between {@link Habit} entities and {@link HabitDTO} objects.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class HabitMapper {
    public abstract HabitDTO toDTO(Habit habit);
    public abstract Habit toEntity(HabitDTO habitDTO);
}
