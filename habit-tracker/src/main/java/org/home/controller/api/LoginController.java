package org.home.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.home.annotations.LoggableUserAction;
import org.home.dto.LoginInputDTO;
import org.home.dto.LoginOutputDTO;
import org.home.model.User;
import org.home.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

/**
 * REST controller for handling user login.
 */
@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
@LoggableUserAction
public class LoginController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /**
     * Authenticates a user with the provided login credentials.
     *
     * @param inputDTO the login credentials including username and password
     * @return a response entity containing the authentication token or an error message if authentication fails
     */
    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginInputDTO inputDTO) {
        User user = userService.findUserByEmail(inputDTO.getUsername());
        if (user != null) {
            if (user.isBlocked()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "User is blocked. Access denied."));
            }
        }

        var authentication = new UsernamePasswordAuthenticationToken(
                inputDTO.getUsername(), inputDTO.getPassword());

        authenticationManager.authenticate(authentication);

        String token = UUID.randomUUID().toString();
        return ResponseEntity.ok(new LoginOutputDTO(token));
    }
}
