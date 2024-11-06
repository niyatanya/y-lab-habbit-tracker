package org.home.repository;

import org.home.model.User;
import java.util.Map;
import java.util.Optional;

/**
 * The {@code UserRepository} interface provides methods for managing user data in the database.
 */
public interface UserRepository {

    /**
     * Retrieves all users from the database.
     *
     * @return a map of user emails to {@link User} objects
     */
    Map<String, User> getEntities();

    /**
     * Saves a new user to the database.
     *
     * @param user the {@link User} to be saved
     */
    void save(User user);

    /**
     * Updates an existing user in the database.
     *
     * @param user the {@link User} to update
     * @return {@code true} if the update was successful; {@code false} otherwise
     */
    boolean update(User user);

    /**
     * Deletes a user from the database.
     *
     * @param user the {@link User} to delete
     * @return {@code true} if the deletion was successful; {@code false} otherwise
     */
    boolean delete(User user);

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to find
     * @return an {@link Optional} containing the {@link User} if found, or an empty {@link Optional}
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks if an email is already registered in the database.
     *
     * @param email the email address to check
     * @return {@code true} if the email is already registered; {@code false} otherwise
     */
    boolean emailIsAlreadyRegistered(String email);
}
