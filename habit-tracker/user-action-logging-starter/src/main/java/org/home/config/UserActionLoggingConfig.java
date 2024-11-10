package org.home.config;

import org.home.logging.LoggableUserActionAspect;
import org.home.repository.AuditRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserActionLoggingConfig {

    @Bean
    public LoggableUserActionAspect loggableUserActionAspect(AuditRepository auditRepository) {
        return new LoggableUserActionAspect(auditRepository);
    }
}
