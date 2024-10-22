package org.home.repository;

import org.home.config.DBConnectionProvider;
import org.home.model.Role;
import org.home.model.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The {@code UserRepository} class provides methods for managing user data in the database.
 */
public class UserRepository {

    private static DBConnectionProvider connectionProvider;

    /**
     * Constructs a new {@code UserRepository} with the provided database connection provider.
     *
     * @param connectionProvider the {@link DBConnectionProvider} used to establish database connections
     */
    public UserRepository(DBConnectionProvider connectionProvider) {
        UserRepository.connectionProvider = connectionProvider;
    }

    /**
     * Retrieves all users from the database.
     *
     * @return a map of user emails to {@link User} objects
     */
    public static Map<String, User> getEntities() {
        String sql = "SELECT * FROM ylab_schema.users";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet resultSet = pstmt.executeQuery();

            Map<String, User> result = new HashMap<>();
            while (resultSet.next()) {
                result.put(resultSet.getString("email"),
                        getUserFromResultSet(resultSet));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
            }
        return new HashMap<>();
    }

    /**
     * Saves a new user to the database.
     *
     * @param user the {@link User} to be saved
     */
    public static void save(User user) {
        String sql = "INSERT INTO ylab_schema.users (name, email, password, role) VALUES (?, ?, ?, ?::ROLE)";
        try (Connection conn = connectionProvider.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setObject(4, user.getRole().name());
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        }
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to find
     * @return an {@link Optional} containing the {@link User} if found, or an empty {@link Optional}
     */
    public static Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM ylab_schema.users WHERE email = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Checks if an email is already registered in the database.
     *
     * @param email the email address to check
     * @return {@code true} if the email is already registered; {@code false} otherwise
     */
    public static boolean emailIsAlreadyRegistered(String email) {
        String sql = "SELECT * FROM ylab_schema.users WHERE email = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        }
        return false;
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user the {@link User} to update
     * @return {@code true} if the update was successful; {@code false} otherwise
     */
    public static boolean update(User user) {
        String sql = "UPDATE ylab_schema.users SET name = ?, email = ?, password = ?, is_blocked = ? WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setBoolean(4, user.isBlocked());
            pstmt.setLong(5, user.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a user from the database.
     *
     * @param user the {@link User} to delete
     * @return {@code true} if the deletion was successful; {@code false} otherwise
     */
    public static boolean delete(User user) {
        String sql = "DELETE FROM ylab_schema.users WHERE email = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    private static User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        Role role = Role.valueOf(resultSet.getString("role"));
        boolean isBlocked = resultSet.getBoolean("is_blocked");
        return new User(id, name, email, password, role, isBlocked);
    }
}
