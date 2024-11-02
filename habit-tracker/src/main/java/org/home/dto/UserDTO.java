package org.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for user information.
 */
@AllArgsConstructor
@Getter
@Setter
public class UserDTO {
    private String name;
    private String email;
}
