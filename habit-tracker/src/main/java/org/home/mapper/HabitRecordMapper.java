package org.home.mapper;

import org.home.dto.HabitRecordDTO;
import org.home.model.HabitRecord;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between {@link HabitRecord} entities and {@link HabitRecordDTO} objects.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class HabitRecordMapper {
    public abstract HabitRecordDTO toDTO(HabitRecord record);
    public abstract HabitRecord toEntity(HabitRecordDTO recordDTO);
}
