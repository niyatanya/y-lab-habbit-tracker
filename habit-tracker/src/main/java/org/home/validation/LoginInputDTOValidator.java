package org.home.validation;

import org.home.dto.LoginInputDTO;

/**
 * Validator for {@link LoginInputDTO} that checks the validity of login input fields.
 */
public class LoginInputDTOValidator {

    /**
     * Validates the given {@link LoginInputDTO}.
     *
     * @param dto the {@code LoginInputDTO} to validate
     * @throws IllegalArgumentException if the email or password fields do not meet validation criteria
     */
    public static void validate(LoginInputDTO dto) {
        validateEmail(dto.getEmail());
        validatePassword(dto.getPassword());
    }

    /**
     * Validates the email field.
     *
     * @param email the email to validate
     * @throws IllegalArgumentException if the email is {@code null} or does not match the expected format
     */
    private static void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    /**
     * Validates the password field.
     *
     * @param password the password to validate
     * @throws IllegalArgumentException if the password is {@code null} or shorter than 8 characters
     */
    private static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
    }
}
