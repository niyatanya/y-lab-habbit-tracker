package org.home.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.home.logging.annotations.LoggableUserAction;
import org.home.dto.ErrorResponseDTO;
import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user registration.
 */
@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
@LoggableUserAction
public class RegisterController {

    private final UserService userService;

    /**
     * Registers a new user with the provided user details.
     *
     * @param userCreateDTO the details of the user to be registered
     * @return a response entity containing the created user details or an error message if registration fails
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO newUserDTO = userService.register(userCreateDTO);
        if (newUserDTO == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponseDTO("Email is already registered"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newUserDTO);
    }
}
