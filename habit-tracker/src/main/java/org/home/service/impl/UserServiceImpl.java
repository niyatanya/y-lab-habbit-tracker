package org.home.service.impl;

import lombok.RequiredArgsConstructor;
import org.home.annotations.LoggableUserAction;
import org.home.dto.UserCreateDTO;
import org.home.dto.UserDTO;
import org.home.mapper.UserMapper;
import org.home.model.User;
import org.home.repository.UserRepository;
import org.home.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.home.model.Role.ADMIN;
import static org.home.model.Role.USER;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
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

    @Override
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

    @Override
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

    @Override
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

    @Override
    public Map<String, UserDTO> getAllUsers() {
        Map<String, User> userMap = userRepository.getEntities();
        return userMap.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> userMapper.toDTO(entry.getValue())
                ));
    }

    @Override
    @LoggableUserAction
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

    @Override
    public boolean blockUser(User user) {
        if (user.isBlocked()) {
            return false;
        } else {
            user.setBlocked(true);
            userRepository.update(user);
            return true;
        }
    }

    @Override
    public boolean unblockUser(User user) {
        if (!user.isBlocked()) {
            return false;
        } else {
            user.setBlocked(false);
            userRepository.update(user);
            return true;
        }
    }
}
