package org.home.service;

import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.model.User;
import java.util.Map;

/**
 * The {@code UserService} interface provides methods for user management operations.
 */
public interface UserService {

    /**
     * Registers a new user.
     *
     * @param dto The data transfer object containing user details.
     * @return The created {@link UserDTO} if registration is successful;
     * {@code null} if the email is already registered.
     */
    UserDTO register(UserCreateDTO dto);

    /**
     * Logs in a user with the provided email and password.
     *
     * @param email    the email of the user for log in
     * @param password the password of the user for log in
     * @return the logged-in {@link User} if successful;
     * returns null if the user is not found, blocked, or if the password is incorrect
     */
    User login(String email, String password);

    /**
     * Edits the profile information of a user.
     *
     * @param oldEmail The current email of the user.
     * @param dto The data transfer object containing the updated profile information.
     * @return The updated {@link UserDTO}; {@code null} if the new email is already registered by some user.
     */
    UserDTO editProfile(String oldEmail, UserCreateDTO dto);

    /**
     * Deletes a user account.
     *
     * @param email The email of the user to be deleted.
     * @return {@code true} if the user was successfully deleted; {@code false} if the user is an admin.
     */
    boolean deleteUser(String email);

    /**
     * Retrieves all registered users.
     *
     * @return A map of user emails to {@link UserDTO} objects.
     */
    Map<String, UserDTO> getAllUsers();

    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user to find
     * @return the found {@link User} if they exist
     */
    User findUserByEmail(String email);

    /**
     * Blocks a user account.
     *
     * @param user the {@link User} to be blocked
     * @return a message indicating the result of the operation
     */
    boolean blockUser(User user);

    /**
     * Unblocks a user account.
     *
     * @param user the {@link User} to be unblocked
     * @return a message indicating the result of the operation
     */
    boolean unblockUser(User user);

    /**
     * Validates the password of a user.
     *
     * @param user The {@link User} whose password is being validated.
     * @param password The password to validate.
     * @return {@code true} if the password is correct; {@code false} otherwise.
     */
    boolean validatePassword(User user, String password);
}
