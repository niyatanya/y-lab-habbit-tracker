package org.home.service;

import org.home.model.Role;
import org.home.model.User;
import org.home.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.home.model.Role.ADMIN;

public class UserService {

    public User register(String name, String email, String password) {
        if (UserRepository.emailIsAlreadyRegistered(email)) {
            return null;
        }

        User newUser = new User(name, email, password, Role.USER);
        UserRepository.save(newUser);
        return newUser;
    }

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

    public void editProfile(User user, String newName, String newEmail, String newPassword) {
        if (!user.getEmail().equals(newEmail) && UserRepository.emailIsAlreadyRegistered(newEmail)) {
            return;
        }

        user.setName(newName);
        user.setEmail(newEmail);
        user.setPassword(newPassword);
        UserRepository.update(user);
    }

    public void deleteUser(User user) {
        if (user.getRole().equals(ADMIN)) {
            System.out.println("Cannot delete an admin user.");
        } else {
            UserRepository.delete(user);
        }
    }

    public Map<String, User> getAllUsers() {
        return new HashMap<>(UserRepository.getEntities());
    }

    public User findUserByEmail(String email) {
        return UserRepository.findByEmail(email).orElseThrow();
    }

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
