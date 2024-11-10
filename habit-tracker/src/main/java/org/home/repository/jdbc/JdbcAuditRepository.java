package org.home.repository.jdbc;

import lombok.RequiredArgsConstructor;
import org.home.model.UserAuditLog;
import org.home.repository.AuditRepository;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcAuditRepository implements AuditRepository {

    private final DataSource dataSource;

    @Override
    public void saveAuditLog(UserAuditLog auditLog) {
        ResultSet generatedKeys = null;
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     SqlQueries.INSERT_AUDIT_LOG, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, auditLog.getEmail());
            pstmt.setString(2, auditLog.getAction());
            pstmt.setString(3, auditLog.getResult());
            pstmt.setTimestamp(4, auditLog.getTimestamp());
            pstmt.executeUpdate();
            generatedKeys = pstmt.getGeneratedKeys();
            if (generatedKeys.next()) {
                auditLog.setId(generatedKeys.getLong(1));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception in saveAuditLog: " + e.getMessage());
        } finally {
            closeResultSet(generatedKeys);
        }
    }

    @Override
    public List<UserAuditLog> getAllAuditLogs() {
        ResultSet resultSet = null;
        List<UserAuditLog> auditLogs = new LinkedList<>();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(SqlQueries.SELECT_ALL_AUDIT_LOGS)) {

            resultSet = pstmt.executeQuery();

            while (resultSet.next()) {
                auditLogs.add(getAuditLogFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("Got SQL Exception in getAllAuditLogs: " + e.getMessage());
        } finally {
            closeResultSet(resultSet);
        }
        return auditLogs;
    }

    private UserAuditLog getAuditLogFromResultSet(ResultSet resultSet) throws SQLException {
        String email = resultSet.getString("email");
        String action = resultSet.getString("action");
        String result = resultSet.getString("result");
        Timestamp timestamp = resultSet.getTimestamp("timestamp");
        return new UserAuditLog(email, action, result, timestamp);
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
