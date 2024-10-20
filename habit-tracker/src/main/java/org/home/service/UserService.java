package org.home.service;

import org.home.model.Role;
import org.home.model.User;
import org.home.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.home.model.Role.ADMIN;

/**
 * The {@code UserService} class provides methods for user management operations.
 */
public class UserService {

    /**
     * Registers a new user with given parameters.
     *
     * @param name     the name of the new user
     * @param email    the email of the new user
     * @param password the password of the new user
     * @return the newly created {@link User} if registration is successful;
     * returns null if the email is already registered
     */
    public User register(String name, String email, String password) {
        if (UserRepository.emailIsAlreadyRegistered(email)) {
            return null;
        }

        User newUser = new User(name, email, password, Role.USER);
        UserRepository.save(newUser);
        return newUser;
    }

    /**
     * Logs in a user with the provided email and password.
     *
     * @param email    the email of the user for log in
     * @param password the password of the user for log in
     * @return the logged-in {@link User} if successful;
     * returns null if the user is not found, blocked, or if the password is incorrect
     */
    public User login(String email, String password) {
        Optional<User> maybeUser = UserRepository.findByEmail(email);

        if (maybeUser.isEmpty()) {
            return null;
        }

        User user = maybeUser.get();
        if (user.isBlocked()) {
            System.out.println("This account is blocked.");
            return null;
        } else if (user.getPassword().equals(password)) {
            return user;
        }
        return null;
    }

    /**
     * Edits the profile of a user.
     *
     * @param user        the {@link User} whose profile is to be edited
     * @param newName     the new name for the user
     * @param newEmail    the new email for the user
     * @param newPassword  the new password for the user
     */
    public void editProfile(User user, String newName, String newEmail, String newPassword) {
        if (!user.getEmail().equals(newEmail) && UserRepository.emailIsAlreadyRegistered(newEmail)) {
            return;
        }

        user.setName(newName);
        user.setEmail(newEmail);
        user.setPassword(newPassword);
        UserRepository.update(user);
    }

    /**
     * Deletes a user account.
     *
     * @param user the {@link User} to be deleted
     */
    public void deleteUser(User user) {
        if (user.getRole().equals(ADMIN)) {
            System.out.println("Cannot delete an admin user.");
        } else {
            UserRepository.delete(user);
        }
    }

    /**
     * Retrieves all users in the system.
     *
     * @return a map of all {@link User} entities
     */
    public Map<String, User> getAllUsers() {
        return new HashMap<>(UserRepository.getEntities());
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user to find
     * @return the found {@link User} if they exist
     */
    public User findUserByEmail(String email) {
        return UserRepository.findByEmail(email).orElseThrow();
    }

    /**
     * Blocks a user account.
     *
     * @param user the {@link User} to be blocked
     * @return a message indicating the result of the operation
     */
    public String blockUser(User user) {
        if (user.isBlocked()) {
            return "User is already blocked.";
        } else if (user.getRole().equals(ADMIN)) {
            return "Cannot block an admin user.";
        } else {
            user.setBlocked(true);
            UserRepository.update(user);
            return "User " + user.getName() + " has been blocked.";
        }
    }

    /**
     * Unblocks a user account.
     *
     * @param user the {@link User} to be unblocked
     * @return a message indicating the result of the operation
     */
    public String unblockUser(User user) {
        if (!user.isBlocked()) {
            return "User is already unblocked.";
        } else {
            user.setBlocked(false);
            UserRepository.update(user);
            return "User " + user.getName() + " has been unblocked.";
        }
    }
}
