package org.home.repository;

import org.home.model.UserAuditLog;
import java.util.List;

/**
 * Repository interface for managing user audit logs.
 */
public interface AuditRepository {

    /**
     * Saves a user audit log entry to the database.
     *
     * @param auditLog the audit log entry to save
     */
    void saveAuditLog(UserAuditLog auditLog);

    /**
     * Retrieves all user audit log entries from the database.
     *
     * @return a list of all user audit logs
     */
    List<UserAuditLog> getAllAuditLogs();
}
