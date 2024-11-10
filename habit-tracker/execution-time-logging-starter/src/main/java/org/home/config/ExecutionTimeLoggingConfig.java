package org.home.config;

import org.home.logging.AllMethodsExecutionTimeAspect;
import org.home.logging.annotations.EnableExecutionTimeLogging;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class that sets up the {@link AllMethodsExecutionTimeAspect} bean.
 * The aspect is enabled only if the {@code @EnableExecutionTimeLogging} annotation
 * is present in the application context.
 */
@Configuration
@ConditionalOnBean(annotation = EnableExecutionTimeLogging.class)
public class ExecutionTimeLoggingConfig {

    /**
     * Creates an instance of {@link AllMethodsExecutionTimeAspect} to log execution times.
     *
     * @return an instance of {@link AllMethodsExecutionTimeAspect}
     */
    @Bean
    public AllMethodsExecutionTimeAspect allMethodsExecutionTimeAspect() {
        return new AllMethodsExecutionTimeAspect();
    }
}
