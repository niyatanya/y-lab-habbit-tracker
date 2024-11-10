package org.home.config;

import org.home.logging.LoggableUserActionAspect;
import org.home.repository.AuditRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that sets up the {@link LoggableUserActionAspect} bean.
 */
@Configuration
public class UserActionLoggingConfig {

    /**
     * Creates an instance of {@link LoggableUserActionAspect} to log user actions.
     *
     * @return an instance of {@link LoggableUserActionAspect}
     */
    @Bean
    public LoggableUserActionAspect loggableUserActionAspect(AuditRepository auditRepository) {
        return new LoggableUserActionAspect(auditRepository);
    }
}
