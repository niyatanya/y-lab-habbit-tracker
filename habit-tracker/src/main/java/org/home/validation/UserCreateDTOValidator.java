package org.home.validation;

import org.home.dto.UserCreateDTO;

/**
 * Validator for {@link UserCreateDTO} that verifies the validity of user input fields.
 */
public class UserCreateDTOValidator {

    /**
     * Validates the provided {@link UserCreateDTO}.
     *
     * @param dto the {@code UserCreateDTO} to validate
     * @throws IllegalArgumentException if any field does not meet validation criteria
     */
    public static void validate(UserCreateDTO dto) {
        validateName(dto.getName());
        validateEmail(dto.getEmail());
        validatePassword(dto.getPassword());
    }

    /**
     * Validates the name field.
     *
     * @param name the name to validate
     * @throws IllegalArgumentException if the name is {@code null} or does not meet validation criteria
     */
    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.length() < 3 || name.length() > 50) {
            throw new IllegalArgumentException("Name must be between 3 and 50 characters");
        }
    }

    /**
     * Validates the email field.
     *
     * @param email the email to validate
     * @throws IllegalArgumentException if the email is {@code null} or does not meet validation criteria
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
     * @throws IllegalArgumentException if the password is {@code null} or does not meet validation criteria
     */
    private static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
    }
}
