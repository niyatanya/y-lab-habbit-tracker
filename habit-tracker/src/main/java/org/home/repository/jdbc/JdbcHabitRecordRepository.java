package org.home.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.home.model.Habit;
import org.home.model.HabitRecord;
import org.home.repository.HabitRecordRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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

@Repository
@RequiredArgsConstructor
public class JdbcHabitRecordRepository implements HabitRecordRepository {

    private final DataSource dataSource;

    @Override
    public Map<LocalDate, HabitRecord> getAllHabitRecords(Habit habit) {
        ResultSet resultSet = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_ALL_RECORDS)) {
            pstmt.setLong(1, habit.getId());
            resultSet = pstmt.executeQuery();

            Map<LocalDate, HabitRecord> result = new HashMap<>();
            while (resultSet.next()) {
                result.put(resultSet.getDate("date").toLocalDate(),
                        getRecordFromResultSet(resultSet));
            }
            return result;
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        } finally {
            closeResultSet(resultSet);
        }
        return new HashMap<>();
    }

    @Override
    public void save(HabitRecord record) {
        ResultSet generatedKeys = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     SqlQueries.INSERT_RECORD, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setDate(1, Date.valueOf(record.getDate()));
            pstmt.setBoolean(2, record.isCompleted());
            pstmt.setObject(3, record.getHabitId());
            pstmt.executeUpdate();
            generatedKeys = pstmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                record.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        } finally {
            closeResultSet(generatedKeys);
        }
    }

    @Override
    public boolean recordExists(Long habitId, LocalDate date) {
        ResultSet resultSet = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_RECORD)) {
            pstmt.setLong(1, habitId);
            pstmt.setDate(2, Date.valueOf(date));
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        } finally {
            closeResultSet(resultSet);
        }
        return false;
    }

    @Override
    public Optional<HabitRecord> findByDateAndHabitId(LocalDate date, Long habitId) {
        ResultSet resultSet = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_RECORD)) {
            pstmt.setLong(1, habitId);
            pstmt.setDate(2, Date.valueOf(date));
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getRecordFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        } finally {
            closeResultSet(resultSet);
        }
        return Optional.empty();
    }

    @Override
    public boolean update(HabitRecord record) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.UPDATE_RECORD)) {
            pstmt.setBoolean(1, record.isCompleted());
            pstmt.setLong(2, record.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(HabitRecord record) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.DELETE_RECORD)) {
            pstmt.setLong(1, record.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
            return false;
        }
    }

    private HabitRecord getRecordFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        LocalDate date = resultSet.getDate("date").toLocalDate();
        boolean completed = resultSet.getBoolean("completed");
        Long habitId = resultSet.getLong("habit_id");
        return new HabitRecord(id, date, completed, habitId);
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
