package org.home.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for login input.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginInputDTO {

    @NotNull
    @Email
    private String username;

    @NotBlank
    private String password;
}
