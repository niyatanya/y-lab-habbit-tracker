package org.home.repository;

import lombok.RequiredArgsConstructor;
import org.home.model.Role;
import org.home.model.User;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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
@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final DataSource dataSource;

    /**
     * Retrieves all users from the database.
     *
     * @return a map of user emails to {@link User} objects
     */
    public Map<String, User> getEntities() {
        ResultSet resultSet = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_ALL_USERS)) {
            resultSet = pstmt.executeQuery();

            Map<String, User> result = new HashMap<>();
            while (resultSet.next()) {
                result.put(resultSet.getString("email"),
                        getUserFromResultSet(resultSet));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        } finally {
            closeResultSet(resultSet);
        }
        return new HashMap<>();
    }

    /**
     * Saves a new user to the database.
     *
     * @param user the {@link User} to be saved
     */
    public void save(User user) {
        ResultSet generatedKeys = null;
        try (Connection conn = dataSource.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(
                        SqlQueries.INSERT_USER, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getName());
            pstmt.setString(2, user.getEmail());
            pstmt.setString(3, user.getPassword());
            pstmt.setObject(4, user.getRole().name());
            pstmt.executeUpdate();
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                user.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        } finally {
            closeResultSet(generatedKeys);
        }
    }

    /**
     * Finds a user by their email address.
     *
     * @param email the email address of the user to find
     * @return an {@link Optional} containing the {@link User} if found, or an empty {@link Optional}
     */
    public Optional<User> findByEmail(String email) {
        ResultSet resultSet = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_USER)) {
            pstmt.setString(1, email);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        } finally {
            closeResultSet(resultSet);
        }
        return Optional.empty();
    }

    /**
     * Checks if an email is already registered in the database.
     *
     * @param email the email address to check
     * @return {@code true} if the email is already registered; {@code false} otherwise
     */
    public boolean emailIsAlreadyRegistered(String email) {
        ResultSet resultSet = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_USER)) {
            pstmt.setString(1, email);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        }  finally {
            closeResultSet(resultSet);
        }
        return false;
    }

    /**
     * Updates an existing user in the database.
     *
     * @param user the {@link User} to update
     * @return {@code true} if the update was successful; {@code false} otherwise
     */
    public boolean update(User user) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.UPDATE_USER)) {
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
    public boolean delete(User user) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.DELETE_USER)) {
            pstmt.setString(1, user.getEmail());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            return false;
        }
    }

    private User getUserFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        Role role = Role.valueOf(resultSet.getString("role"));
        boolean isBlocked = resultSet.getBoolean("is_blocked");
        return new User(id, name, email, password, role, isBlocked);
    }

    private void closeResultSet(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                System.out.println("Failed to close result set: " + e.getMessage());
            }
        }
    }
}
