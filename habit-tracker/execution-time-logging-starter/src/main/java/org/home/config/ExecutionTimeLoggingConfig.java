package org.home.config;

import org.home.logging.AllMethodsExecutionTimeAspect;
import org.home.logging.annotations.EnableExecutionTimeLogging;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(annotation = EnableExecutionTimeLogging.class)
public class ExecutionTimeLoggingConfig {

    @Bean
    public AllMethodsExecutionTimeAspect allMethodsExecutionTimeAspect() {
        return new AllMethodsExecutionTimeAspect();
    }
}
