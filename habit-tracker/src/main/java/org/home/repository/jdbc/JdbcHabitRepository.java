package org.home.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.home.model.Frequency;
import org.home.model.Habit;
import org.home.model.User;
import org.home.repository.HabitRepository;
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

@Repository
@RequiredArgsConstructor
public class JdbcHabitRepository implements HabitRepository {

    private final DataSource dataSource;

    @Override
    public Map<String, Habit> getAllUserHabits(User user) {
        ResultSet resultSet = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_ALL_HABITS)) {
            pstmt.setLong(1, user.getId());
            resultSet = pstmt.executeQuery();

            Map<String, Habit> result = new HashMap<>();
            while (resultSet.next()) {
                result.put(resultSet.getString("title"),
                        getHabitFromResultSet(resultSet));
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
    public void save(Habit habit) {
        ResultSet generatedKeys = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     SqlQueries.INSERT_HABIT, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, habit.getTitle());
            pstmt.setString(2, habit.getDescription());
            pstmt.setString(3, habit.getFrequency().name());
            pstmt.setObject(4, habit.getUserId());
            pstmt.executeUpdate();
            generatedKeys = pstmt.getGeneratedKeys();

            if (generatedKeys.next()) {
                habit.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        } finally {
            closeResultSet(generatedKeys);
        }
    }

    @Override
    public boolean habitExists(Long userId, String title) {
        ResultSet resultSet = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_HABIT)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, title);
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
    public Optional<Habit> findByTitleAndUserId(String title, Long userId) {
        ResultSet resultSet = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_HABIT)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, title);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                return Optional.of(getHabitFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception: " + e.getMessage());
        } finally {
            closeResultSet(resultSet);
        }
        return Optional.empty();
    }

    @Override
    public boolean update(Habit habit) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.UPDATE_HABIT)) {
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

    @Override
    public boolean delete(Habit habit) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.DELETE_HABIT)) {
            pstmt.setLong(1, habit.getId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error deleting habit: " + e.getMessage());
            return false;
        }
    }

    private Habit getHabitFromResultSet(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String title = resultSet.getString("title");
        String description = resultSet.getString("description");
        Frequency frequency = Frequency.valueOf(resultSet.getString("frequency"));
        Long userId = resultSet.getLong("user_id");
        return new Habit(id, title, description, frequency, userId);
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
