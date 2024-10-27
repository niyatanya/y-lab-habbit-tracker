package org.home.validation;

import org.home.dto.LoginInputDTO;

public class LoginInputDTOValidator {

    public static void validate(LoginInputDTO dto) {
        validateEmail(dto.getEmail());
        validatePassword(dto.getPassword());
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
