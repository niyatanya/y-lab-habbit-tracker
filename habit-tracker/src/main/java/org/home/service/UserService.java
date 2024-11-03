package org.home.service;

import lombok.RequiredArgsConstructor;
import org.home.annotations.LoggableUserAction;
import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.mapper.UserMapper;
import org.home.model.User;
import org.home.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.home.model.Role.ADMIN;
import static org.home.model.Role.USER;

/**
 * The {@code UserService} class provides methods for user management operations.
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    /**
     * Registers a new user.
     *
     * @param dto The data transfer object containing user details.
     * @return The created {@link UserDTO} if registration is successful;
     * {@code null} if the email is already registered.
     */
    @LoggableUserAction
    public UserDTO register(UserCreateDTO dto) {
        if (userRepository.emailIsAlreadyRegistered(dto.getEmail())) {
            return null;
        }

        User newUser = userMapper.toEntity(dto);
        newUser.setRole(USER);
        userRepository.save(newUser);
        return userMapper.toDTO(newUser);
    }

    /**
     * Logs in a user with the provided email and password.
     *
     * @param email    the email of the user for log in
     * @param password the password of the user for log in
     * @return the logged-in {@link User} if successful;
     * returns null if the user is not found, blocked, or if the password is incorrect
     */
    @LoggableUserAction
    public User login(String email, String password) {
        Optional<User> maybeUser = userRepository.findByEmail(email);

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
     * Edits the profile information of a user.
     *
     * @param oldEmail The current email of the user.
     * @param dto The data transfer object containing the updated profile information.
     * @return The updated {@link UserDTO}; {@code null} if the new email is already registered by some user.
     */
    @LoggableUserAction
    public UserDTO editProfile(String oldEmail, UserCreateDTO dto) {
        if (!oldEmail.equals(dto.getEmail()) && userRepository.emailIsAlreadyRegistered(dto.getEmail())) {
            return null;
        }

        User user = userRepository.findByEmail(oldEmail).orElseThrow();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        userRepository.update(user);
        return userMapper.toDTO(user);
    }

    /**
     * Deletes a user account.
     *
     * @param email The email of the user to be deleted.
     * @return {@code true} if the user was successfully deleted; {@code false} if the user is an admin.
     */
    @LoggableUserAction
    public boolean deleteUser(String email) {
        User user = userRepository.findByEmail(email).orElseThrow();
        if (user.getRole().equals(ADMIN)) {
            return false;
        } else {
            userRepository.delete(user);
            return true;
        }
    }

    /**
     * Retrieves all registered users.
     *
     * @return A map of user emails to {@link UserDTO} objects.
     */
    public Map<String, UserDTO> getAllUsers() {
        Map<String, User> userMap = userRepository.getEntities();
        return userMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> userMapper.toDTO(entry.getValue())
                ));
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the email of the user to find
     * @return the found {@link User} if they exist
     */
    @LoggableUserAction
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
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
            userRepository.update(user);
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
            userRepository.update(user);
            return true;
        }
    }

    /**
     * Validates the password of a user.
     *
     * @param user The {@link User} whose password is being validated.
     * @param password The password to validate.
     * @return {@code true} if the password is correct; {@code false} otherwise.
     */
    @LoggableUserAction
    public boolean validatePassword(User user, String password) {
        return user.getPassword().equals(password);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return findUserByEmail(username);
    }
}
