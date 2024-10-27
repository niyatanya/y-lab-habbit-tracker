package org.home.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.home.annotations.LoggableUserAction;
import org.home.model.Role;
import org.home.model.User;

import java.util.HashMap;
import java.util.UUID;

@LoggableUserAction
@Getter
public class AuthService {
    private static final HashMap<String, UserSession> SESSIONS = new HashMap<>();

    public String loginUser(User user) {
        String token = UUID.randomUUID().toString();
        SESSIONS.put(token, new UserSession(user.getEmail(), user.getRole()));
        return token;
    }

    public static UserSession validateToken(String token) {
        return SESSIONS.get(token);
    }

    public static void logoutUser(String token) {
        SESSIONS.remove(token);
    }

    @Getter
    @AllArgsConstructor
    public static class UserSession {
        private final String email;
        private final Role role;
    }
}
