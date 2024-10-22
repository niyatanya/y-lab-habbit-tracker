package org.home.repository;

import org.home.config.DBConnectionProvider;
import org.home.model.Frequency;
import org.home.model.Habit;
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
 * The {@code HabitRepository} class provides methods to manage habits in the database.
 */
public class HabitRepository {
    private static DBConnectionProvider connectionProvider;

    /**
     * Constructs a new {@code HabitRepository} with the provided database connection provider.
     *
     * @param connectionProvider the {@link DBConnectionProvider} used to establish database connections
     */
    public HabitRepository(DBConnectionProvider connectionProvider) {
        HabitRepository.connectionProvider = connectionProvider;
    }

    /**
     * Retrieves all habits associated with a specific user.
     *
     * @param user the {@link User} for whom to retrieve habits
     * @return a map of habit titles to {@link Habit} objects for the specified user
     */
    public static Map<String, Habit> getAllUserHabits(User user) {
        String sql = "SELECT * FROM ylab_schema.habits WHERE user_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, user.getId());
            ResultSet resultSet = pstmt.executeQuery();

            Map<String, Habit> result = new HashMap<>();
            while (resultSet.next()) {
                result.put(resultSet.getString("title"),
                        getHabitFromResultSet(resultSet));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        }
        return new HashMap<>();
    }

    /**
     * Saves a new habit to the database.
     *
     * @param habit the {@link Habit} to be saved
     */
    public static void save(Habit habit) {
        String sql = "INSERT INTO ylab_schema.habits (title, description, frequency, user_id) VALUES"
                + "(?, ?, ?::FREQUENCY, ?)";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, habit.getTitle());
            pstmt.setString(2, habit.getDescription());
            pstmt.setString(3, habit.getFrequency().name());
            pstmt.setObject(4, habit.getUserId());
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                habit.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        }
    }

    /**
     * Checks if a habit exists for a specific user with the given title.
     *
     * @param userId the ID of the user
     * @param title  the title of the habit to check
     * @return {@code true} if the habit exists; {@code false} otherwise
     */
    public static boolean habitExists(Long userId, String title) {
        String sql = "SELECT * FROM ylab_schema.habits WHERE user_id = ? AND title = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, title);
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
     * Finds a habit by its title and associated user ID.
     *
     * @param title  the title of the habit
     * @param userId the ID of the associated user
     * @return an {@link Optional} containing the {@link Habit} if found, or an empty {@link Optional}
     */
    public static Optional<Habit> findByTitleAndUserId(String title, Long userId) {
        String sql = "SELECT * FROM ylab_schema.habits WHERE title = ? AND user_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            pstmt.setLong(2, userId);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getHabitFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Updates an existing habit in the database.
     *
     * @param habit the {@link Habit} to update
     * @return {@code true} if the update was successful; {@code false} otherwise
     */
    public static boolean update(Habit habit) {
        String sql = "UPDATE ylab_schema.habits SET title = ?, description = ?, frequency = ?::FREQUENCY WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, habit.getTitle());
            pstmt.setString(2, habit.getDescription());
            pstmt.setString(3, habit.getFrequency().name());
            pstmt.setLong(4, habit.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating habit: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a habit from the database.
     *
     * @param habit the {@link Habit} to delete
     * @return {@code true} if the deletion was successful; {@code false} otherwise
     */
    public static boolean delete(Habit habit) {
        String sql = "DELETE FROM ylab_schema.habits WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, habit.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting habit: " + e.getMessage());
            return false;
        }
    }

    private static Habit getHabitFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        Frequency frequency = Frequency.valueOf(resultSet.getString("frequency"));
        Long userId = resultSet.getLong("user_id");
        return new Habit(id, title, description, frequency, userId);
    }
}
