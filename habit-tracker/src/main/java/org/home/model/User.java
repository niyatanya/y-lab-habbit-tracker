package org.home.model;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class User {
    private String name;
    private String email;
    private String password;
    private Role role;
    private boolean isBlocked = false;
    private final Map<String, Habit> habits = new HashMap<>();

    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
