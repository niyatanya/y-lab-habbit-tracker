package org.home.mapper;

import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper for converting between {@link User} entities and {@link UserDTO} or {@link UserCreateDTO} objects.
 */
@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class UserMapper {
    public abstract UserDTO toDTO(User user);
    public abstract User toEntity(UserCreateDTO userCreateDTO);
}
