package org.home.controller.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.home.annotations.LoggableUserAction;
import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.model.User;
import org.home.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        Map<String, UserDTO> userMap = userService.getAllUsers();
        List<UserDTO> users = userMap.values().stream().toList();
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
    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(
            @PathVariable("email") String email,
            @Valid @RequestBody UserCreateDTO userCreateDTO) throws AccessDeniedException {

        UserDTO updatedUserDTO = userService.editProfile(email, userCreateDTO);
        return updatedUserDTO != null
                ? ResponseEntity.ok(updatedUserDTO)
                : ResponseEntity.status(409).body(Map.of("error", "Email is already registered"));
    }

    /**
     * Deletes a user identified by their email.
     *
     * @param email the email of the user to be deleted
     * @return a response entity indicating the success or failure of the deletion
     */
    @LoggableUserAction
    @PreAuthorize("#email == authentication.principal.username or hasRole('ADMIN')")
    @DeleteMapping(value = "/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteUser(@PathVariable("email") String email) {
        boolean deleteResult = userService.deleteUser(email);
        return deleteResult
                ? ResponseEntity.noContent().build()
                : ResponseEntity.status(403).body(Map.of("error", "Cannot delete an admin user."));
    }

    /**
     * Performs an administrative action on a user.
     *
     * @param action the action to be performed (block or unblock)
     * @param email the email of the user on whom the action is to be performed
     * @return a response entity indicating the result of the action
     */
    @PutMapping(value = "/{action}/{email}", produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminAction(@PathVariable("action") String action, @PathVariable("email") String email) {
        User user = userService.findUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Cannot find user with this email: " + email));
        }

        boolean success;
        if ("block".equalsIgnoreCase(action)) {
            success = userService.blockUser(user);
        } else if ("unblock".equalsIgnoreCase(action)) {
            success = userService.unblockUser(user);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid action. Use 'block' or 'unblock'."));
        }

        if (success) {
            return ResponseEntity.ok(Map.of("message", "User " + action + "ed successfully."));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found or already " + action + "ed."));
        }
    }
}
