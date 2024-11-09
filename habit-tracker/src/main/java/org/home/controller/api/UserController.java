package org.home.controller.api;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.home.logging.annotations.LoggableUserAction;
import org.home.dto.ErrorResponseDTO;
import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.model.User;
import org.home.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import static org.home.model.Role.ADMIN;

/**
 * REST controller for managing user-related operations.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retrieves all registered users.
     *
     * @return a response entity containing a list of UserDTOs
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, UserDTO>> getAllUsers(HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims == null || !ADMIN.equals(claims.get("role"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Map<String, UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(users.size()))
                .body(users);
    }

    /**
     * Updates the profile of a user identified by their email.
     *
     * @param email the email of the user to be updated
     * @param userCreateDTO the new user data for the update
     * @return a response entity containing the updated user profile or an error message
     */
    @LoggableUserAction
    @PutMapping(value = "/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUser(@PathVariable("email") String email,
                                        @Valid @RequestBody UserCreateDTO userCreateDTO,
                                        HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        String username = (String) claims.get("username");
        if (username == null || (!username.equals(email) && !ADMIN.equals(claims.get("role")))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO("Access denied"));
        }

        UserDTO updatedUserDTO = userService.editProfile(email, userCreateDTO);
        return updatedUserDTO != null
                ? ResponseEntity.ok(updatedUserDTO)
                : ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponseDTO("Email is already registered"));
    }

    /**
     * Deletes a user identified by their email.
     *
     * @param email the email of the user to be deleted
     * @return a response entity indicating the success or failure of the deletion
     */
    @LoggableUserAction
    @DeleteMapping(value = "/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable("email") String email,
                                        HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        String username = (String) claims.get("username");
        if (username == null || (!username.equals(email) && !ADMIN.equals(claims.get("role")))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponseDTO("Access denied"));
        }

        boolean deleteResult = userService.deleteUser(email);
        return deleteResult
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        new ErrorResponseDTO("Cannot delete an admin user."));
    }

    /**
     * A method to block a user.
     *
     * @param email the email of the user to be blocked
     * @return a response entity indicating the result of the action
     */
    @PutMapping(value = "/block/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> blockUser(@PathVariable("email") String email,
                                       HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims == null || !ADMIN.equals(claims.get("role"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Cannot find user with this email: " + email));
        }

        boolean success = userService.blockUser(user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "User blocked successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("User not found or already blocked."));
        }
    }

    /**
     * A method to unblock a user.
     *
     * @param email the email of the user to be unblocked
     * @return a response entity indicating the result of the action
     */
    @PutMapping(value = "/unblock/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> unblockUser(@PathVariable("email") String email,
                                         HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        if (claims == null || !ADMIN.equals(claims.get("role"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        User user = userService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("Cannot find user with this email: " + email));
        }

        boolean success = userService.unblockUser(user);
        if (success) {
            return ResponseEntity.ok(Map.of("message", "User unblocked successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponseDTO("User not found or already unblocked."));
        }
    }
}
