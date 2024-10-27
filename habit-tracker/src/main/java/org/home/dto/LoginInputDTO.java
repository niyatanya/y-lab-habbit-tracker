package org.home.dto;

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
    private String email;
    private String password;
}
