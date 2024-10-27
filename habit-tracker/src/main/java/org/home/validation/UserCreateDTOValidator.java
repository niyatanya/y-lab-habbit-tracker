package org.home.validation;

import org.home.dto.UserCreateDTO;

public class UserCreateDTOValidator {

    public static void validate(UserCreateDTO dto) {
        validateName(dto.getName());
        validateEmail(dto.getEmail());
        validatePassword(dto.getPassword());
    }

    private static void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be empty");
        }
        if (name.length() < 3 || name.length() > 50) {
            throw new IllegalArgumentException("Name must be between 3 and 50 characters");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || !email.matches("^[\\w-.]+@[\\w-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters long");
        }
    }
}
