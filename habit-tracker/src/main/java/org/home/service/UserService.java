package org.home.service;

import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.mapper.UserMapper;
import org.home.model.User;
import org.home.repository.UserRepository;
import org.mapstruct.factory.Mappers;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.home.model.Role.ADMIN;
import static org.home.model.Role.USER;

/**
 * The {@code UserService} class provides methods for user management operations.
 */
public class UserService {

    private static final UserMapper MAPPER = Mappers.getMapper(UserMapper.class);

    /**
     * Registers a new user with given parameters.
     *
     * @return the newly created {@link User} if registration is successful;
     * returns null if the email is already registered
     */
    public UserDTO register(UserCreateDTO dto) {
        if (UserRepository.emailIsAlreadyRegistered(dto.getEmail())) {
            return null;
        }

        User newUser = MAPPER.toEntity(dto);
        newUser.setRole(USER);
        UserRepository.save(newUser);
        return MAPPER.toDTO(newUser);
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
     */
    public UserDTO editProfile(String oldEmail, UserCreateDTO dto) {
        if (!oldEmail.equals(dto.getEmail()) && UserRepository.emailIsAlreadyRegistered(dto.getEmail())) {
            return null;
        }

        User user = UserRepository.findByEmail(oldEmail).orElseThrow();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        UserRepository.update(user);
        return MAPPER.toDTO(user);
    }

    /**
     * Deletes a user account.
     *
     */
    public boolean deleteUser(String email) {
        User user = UserRepository.findByEmail(email).orElseThrow();
        if (user.getRole().equals(ADMIN)) {
            return false;
        } else {
            UserRepository.delete(user);
            return true;
        }
    }

    /**
     * Retrieves all users in the system.
     *
     * @return a map of all {@link User} entities
     */
    public Map<String, UserDTO> getAllUsers() {
        Map<String, User> userMap = UserRepository.getEntities();
        return userMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> MAPPER.toDTO(entry.getValue())
                ));
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
    public boolean blockUser(User user) {
        if (user.isBlocked()) {
            return false;
        } else {
            user.setBlocked(true);
            UserRepository.update(user);
            return true;
        }
    }

    /**
     * Unblocks a user account.
     *
     * @param user the {@link User} to be unblocked
     * @return a message indicating the result of the operation
     */
    public boolean unblockUser(User user) {
        if (!user.isBlocked()) {
            return false;
        } else {
            user.setBlocked(false);
            UserRepository.update(user);
            return true;
        }
    }

    public boolean validatePassword(User user, String password) {
        return user.getPassword().equals(password);
    }
}
