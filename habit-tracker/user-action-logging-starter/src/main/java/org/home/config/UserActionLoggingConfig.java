package org.home.config;

import org.home.logging.LoggableUserActionAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserActionLoggingConfig {

    @Bean
    public LoggableUserActionAspect loggableUserActionAspect() {
        return new LoggableUserActionAspect();
    }
}
