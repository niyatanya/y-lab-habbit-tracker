package org.home.service;

import org.home.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserService {
    private final Map<String, User> users = new HashMap<>();

    public User register(String name, String email, String password) {
        if (users.containsKey(email)) {
            return null;
        }

        User newUser = new User(name, email, password);
        users.put(email, newUser);
        return newUser;
    }

    public User login(String email, String password) {
        User user = users.get(email);
        if (user != null && user.getPassword().equals(password)) {
            return user;
        } else {
            return null;
        }
    }

    public void editProfile(User user, String newName, String newEmail, String newPassword) {
        if (!user.getEmail().equals(newEmail) && users.containsKey(newEmail)) {
            return;
        }

        users.remove(user.getEmail());
        user.setName(newName);
        user.setEmail(newEmail);
        user.setPassword(newPassword);
        users.put(newEmail, user);
    }

    public void deleteUser(User user) {
        users.remove(user.getEmail());
    }

    public void resetPassword(String email, String newPassword) {
        User user = users.get(email);
        if (user != null) {
            user.setPassword(newPassword);
            System.out.println("Password reset successfully for: " + email);
        } else {
            System.out.println("Password reset failed: User not found.");
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }
}
