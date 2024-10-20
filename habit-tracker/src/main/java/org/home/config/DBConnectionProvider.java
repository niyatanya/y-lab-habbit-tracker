package org.home.config;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * The {@code DBConnectionProvider} class is responsible for creating and providing
 * a database connection using the provided database URL, username, and password.
 */
public class DBConnectionProvider {

    private final String url;
    private final String username;
    private final String password;

    /**
     * Constructs a {@code DBConnectionProvider} with the specified database URL, username, and password.
     *
     * @param url      the database URL
     * @param username the username for the database
     * @param password the password for the database
     */
    public DBConnectionProvider(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    /**
     * Establishes and returns a {@link Connection} to the database using the provided
     * URL, username, and password.
     *
     * @return a {@link Connection} object that represents the database connection
     */
    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
