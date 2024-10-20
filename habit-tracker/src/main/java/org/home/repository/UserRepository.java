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

public class UserRepository {

    private static DBConnectionProvider connectionProvider;

    public UserRepository(DBConnectionProvider connectionProvider) {
        UserRepository.connectionProvider = connectionProvider;
    }

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
