package org.home.service;

import org.home.model.Role;
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

        Role initialRole = new Role("USER");
        User newUser = new User(name, email, password, initialRole);
        users.put(email, newUser);
        return newUser;
    }

    public User login(String email, String password) {
        User user = users.get(email);

        if (user != null) {
            if (user.isBlocked()) {
                System.out.println("This account is blocked.");
                return null;
            }
            if (user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
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
        if (user.getRole().isAdmin()) {
            System.out.println("Cannot delete an admin user.");
        } else {
            users.remove(user.getEmail());
            System.out.println("User " + user.getName() + " has been deleted.");
        }
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User findUserByEmail(String email) {
        return users.get(email);
    }

    public String blockUser(User user) {
        if (user.isBlocked()) {
            return "User is already blocked.";
        } else if (user.getRole().isAdmin()) {
            return "Cannot block an admin user.";
        } else {
            user.setBlocked(true);
            return "User " + user.getName() + " has been blocked.";
        }
    }

    public String unblockUser(User user) {
        if (!user.isBlocked()) {
            return "User is already unblocked.";
        } else {
            user.setBlocked(false);
            return "User " + user.getName() + " has been unblocked.";
        }
    }
}
