package org.home.logging;

import io.jsonwebtoken.Claims;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.home.model.UserAuditLog;
import org.home.repository.AuditRepository;
import org.home.utils.JwtContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.Timestamp;
import java.time.LocalDateTime;

/**
 * An aspect that logs user actions for methods or classes annotated with
 * corresponding annotation.
 */
@Aspect
public class LoggableUserActionAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggableUserActionAspect.class);
    private final AuditRepository auditRepository;

    public LoggableUserActionAspect(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    /**
     * Pointcut that matches methods or classes annotated with
     * {@link org.home.logging.annotations.LoggableUserAction}.
     */
    @Pointcut("within(@org.home.logging.annotations.LoggableUserAction *)")
    public void annotatedByLoggableUserAction() {}

    /**
     * Logs a successful user action after the method execution.
     *
     * @param joinPoint the join point representing the method execution
     * @param result the result returned by the method
     */
    @AfterReturning(pointcut = "annotatedByLoggableUserAction()", returning = "result")
    public void logSuccess(JoinPoint joinPoint, Object result) {
        UserAuditLog audit = new UserAuditLog();
        audit.setEmail(getUsernameFromJwtClaims());
        audit.setAction(joinPoint.getSignature().toShortString());
        audit.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        audit.setResult("SUCCESS");

        auditRepository.saveAuditLog(audit);
    }

    /**
     * Logs a failed user action when an exception is thrown during the method execution.
     *
     * @param joinPoint the join point representing the method execution
     * @param exception the exception thrown by the method
     */
    @AfterThrowing(pointcut = "annotatedByLoggableUserAction()", throwing = "exception")
    public void logSuccess(JoinPoint joinPoint, Throwable exception) {
        UserAuditLog audit = new UserAuditLog();
        audit.setEmail(getUsernameFromJwtClaims());
        audit.setAction(joinPoint.getSignature().toShortString());
        audit.setTimestamp(Timestamp.valueOf(LocalDateTime.now()));
        audit.setResult("ERROR: " + exception.getMessage());

        auditRepository.saveAuditLog(audit);
    }

    private String getUsernameFromJwtClaims() {
        Claims claims = JwtContext.getClaims();
        if (claims != null) {
            return claims.get("username", String.class);
        }
        return "unknown user";
    }
}
