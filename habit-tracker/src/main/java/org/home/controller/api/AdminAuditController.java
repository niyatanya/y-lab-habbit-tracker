package org.home.controller.api;

import lombok.RequiredArgsConstructor;
import org.home.model.UserAuditLog;
import org.home.repository.AuditRepository;
import org.home.utils.JwtContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.jsonwebtoken.Claims;
import java.util.List;

import static org.home.model.Role.ADMIN;

/**
 * REST controller for managing user audit logs, accessible only to admins.
 */
@RestController
@RequestMapping("/admin/audit-logs")
@RequiredArgsConstructor
public class AdminAuditController {

    private final AuditRepository auditRepository;

    /**
     * Retrieves all user audit logs from the database.
     * Accessible only to users with the 'ADMIN' role.
     *
     * @return a response entity containing a list of user audit logs.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<UserAuditLog>> getAllAuditLogs() {
        Claims claims = JwtContext.getClaims();
        if (claims == null || !ADMIN.equals(claims.get("role"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<UserAuditLog> auditLogs = auditRepository.getAllAuditLogs();
        return ResponseEntity.ok(auditLogs);
    }
}
