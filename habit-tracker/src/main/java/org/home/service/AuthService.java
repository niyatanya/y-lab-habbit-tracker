package org.home.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.home.annotations.LoggableUserAction;
import org.home.model.Role;
import org.home.model.User;

import java.util.HashMap;
import java.util.UUID;

/**
 * Service for managing user authentication and sessions.
 */
@LoggableUserAction
@Getter
public class AuthService {
    private static final HashMap<String, UserSession> SESSIONS = new HashMap<>();

    /**
     * Logs in a user and creates a new session.
     *
     * @param user The user to log in.
     * @return The generated session token.
     */
    public String loginUser(User user) {
        String token = UUID.randomUUID().toString();
        SESSIONS.put(token, new UserSession(user.getEmail(), user.getRole()));
        return token;
    }

    /**
     * Validates a token and retrieves the associated user session.
     *
     * @param token The authentication token to validate.
     * @return The corresponding {@link UserSession}, or {@code null} if the token is invalid.
     */
    public static UserSession validateToken(String token) {
        return SESSIONS.get(token);
    }

    /**
     * Logs out a user by removing their session.
     *
     * @param token The authentication token of the user to log out.
     */
    public static void logoutUser(String token) {
        SESSIONS.remove(token);
    }

    /**
     * Represents a user's session containing their email and role.
     */
    @Getter
    @AllArgsConstructor
    public static class UserSession {
        private final String email;
        private final Role role;
    }
}
