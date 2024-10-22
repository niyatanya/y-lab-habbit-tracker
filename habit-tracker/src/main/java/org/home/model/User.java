package org.home.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * The {@code User} class represents a user in the application with personal details and role information.
 */
@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private boolean isBlocked = false;

    /**
     * Constructs a new {@code User} with the specified parameters.
     *
     * @param name     the name of the user
     * @param email    the email address of the user
     * @param password the password for the user account
     * @param role     the role of the user
     */
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }
}
