package org.home.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.home.annotations.LoggableUserAction;
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
import java.util.Map;

@RestController
@RequestMapping("/register")
@RequiredArgsConstructor
@LoggableUserAction
public class RegisterController {

    private final UserService userService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        UserDTO newUserDTO = userService.register(userCreateDTO);
        if (newUserDTO == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Email is already registered"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newUserDTO);
    }
}
