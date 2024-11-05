package org.home.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for creating a new user or updating an existing user.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserCreateDTO {

    @NotBlank
    private String name;

    @NotNull
    @Email
    private String email;

    @NotBlank
    private String password;
}
