package org.home.repository;

import org.home.model.UserAuditLog;
import java.util.List;

public interface AuditRepository {
    void saveAuditLog(UserAuditLog auditLog);
    List<UserAuditLog> getAllAuditLogs();
}
