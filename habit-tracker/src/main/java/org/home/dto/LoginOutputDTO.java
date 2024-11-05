package org.home.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object (DTO) for login output.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LoginOutputDTO {
    private String token;
}
