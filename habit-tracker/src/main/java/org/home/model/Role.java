package org.home.model;

/**
 * The {@code Role} enum represents the role of a user in the application.
 * It defines two possible roles:
 * <ul>
 *   <li>{@link #USER} - A regular user with standard privileges.</li>
 *   <li>{@link #ADMIN} - An administrator with elevated privileges.</li>
 * </ul>
 */
public enum Role {
    /**
     * Represents a standard user role with basic access rights.
     */
    USER,

    /**
     * Represents an admin role with elevated permissions.
     */
    ADMIN
}
