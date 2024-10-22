package org.home.repository;

import org.home.config.DBConnectionProvider;
import org.home.model.Habit;
import org.home.model.HabitRecord;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * The {@code HabitRecordRepository} class provides methods to manage habit records in the database.
 */
public class HabitRecordRepository {

    private static DBConnectionProvider connectionProvider;

    /**
     * Constructs a new {@code HabitRecordRepository} with the provided database connection provider.
     *
     * @param connectionProvider the {@link DBConnectionProvider} used to establish database connections
     */
    public HabitRecordRepository(DBConnectionProvider connectionProvider) {
        HabitRecordRepository.connectionProvider = connectionProvider;
    }

    /**
     * Retrieves all habit records associated with a specific habit.
     *
     * @param habit the {@link Habit} for which to retrieve records
     * @return a map of dates to {@link HabitRecord} objects for the specified habit
     */
    public static Map<LocalDate, HabitRecord> getAllHabitRecords(Habit habit) {
        String sql = "SELECT * FROM ylab_schema.records WHERE habit_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, habit.getId());
            ResultSet resultSet = pstmt.executeQuery();

            Map<LocalDate, HabitRecord> result = new HashMap<>();
            while (resultSet.next()) {
                result.put(resultSet.getDate("date").toLocalDate(),
                        getRecordFromResultSet(resultSet));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        }
        return new HashMap<>();
    }

    /**
     * Saves a new habit record to the database.
     *
     * @param record the {@link HabitRecord} to be saved
     */
    public static void save(HabitRecord record) {
        String sql = "INSERT INTO ylab_schema.records (date, completed, habit_id) VALUES (?, ?, ?)";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, Date.valueOf(record.getDate()));
            pstmt.setBoolean(2, record.isCompleted());
            pstmt.setObject(3, record.getHabitId());
            pstmt.executeUpdate();
            ResultSet generatedKeys = pstmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                record.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        }
    }

    /**
     * Checks if a habit record exists for a specific habit on a given date.
     *
     * @param habitId the ID of the habit
     * @param date    the date to check for the habit record
     * @return {@code true} if the record exists; {@code false} otherwise
     */
    public static boolean recordExists(Long habitId, LocalDate date) {
        String sql = "SELECT * FROM ylab_schema.records WHERE habit_id = ? AND date = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, habitId);
            pstmt.setDate(2, Date.valueOf(date));
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
     * Finds a habit record by its date and associated habit ID.
     *
     * @param date    the date of the habit record
     * @param habitId the ID of the associated habit
     * @return an {@link Optional} containing the {@link HabitRecord} if found, or an empty {@link Optional}
     */
    public static Optional<HabitRecord> findByDateAndHabitId(LocalDate date, Long habitId) {
        String sql = "SELECT * FROM ylab_schema.records WHERE date = ? AND habit_id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, Date.valueOf(date));
            pstmt.setLong(2, habitId);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getRecordFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        }
        return Optional.empty();
    }

    /**
     * Updates an existing habit record.
     *
     * @param record the {@link HabitRecord} to update
     * @return {@code true} if the update was successful; {@code false} otherwise
     */
    public static boolean update(HabitRecord record) {
        String sql = "UPDATE ylab_schema.records SET completed = ? WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, record.isCompleted());
            pstmt.setLong(2, record.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a habit record from the database.
     *
     * @param record the {@link HabitRecord} to delete
     * @return {@code true} if the deletion was successful; {@code false} otherwise
     */
    public static boolean delete(HabitRecord record) {
        String sql = "DELETE FROM ylab_schema.records WHERE id = ?";
        try (Connection conn = connectionProvider.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, record.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
            return false;
        }
    }

    private static HabitRecord getRecordFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        LocalDate date = resultSet.getDate("date").toLocalDate();
        boolean completed = resultSet.getBoolean("completed");
        Long habitId = resultSet.getLong("habit_id");
        return new HabitRecord(id, date, completed, habitId);
    }
}
