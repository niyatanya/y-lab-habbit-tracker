package org.home.service;

import org.home.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private final List<User> users = new ArrayList<>();

    public User register(String name, String email, String password) {
        if (findUserByEmail(email) != null) {
            return null;
        }

        User newUser = new User(name, email, password);
        users.add(newUser);
        return newUser;
    }

    public User login(String email, String password) {
        User user = findUserByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    public void editProfile(User user, String newName, String newEmail, String newPassword) {
        User existingUser = findUserByEmail(newEmail);
        if (existingUser != null && !existingUser.equals(user)) {
            return;
        }

        user.setName(newName);
        user.setEmail(newEmail);
        user.setPassword(newPassword);
    }

    public void deleteUser(User user) {
        users.remove(user);
    }

    public void resetPassword(String email, String newPassword) {
        User user = findUserByEmail(email);
        if (user != null) {
            user.setPassword(newPassword);
            System.out.println("Password reset successfully for: " + email);
        } else {
            System.out.println("Password reset failed: User not found.");
        }
    }

    private User findUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equalsIgnoreCase(email))
                .findFirst()
                .orElse(null);
    }

    public List<User> getAllUsers() {
        return users;
    }
}
