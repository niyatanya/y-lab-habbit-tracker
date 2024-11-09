package org.home.logging.annotations;

import org.home.config.ExecutionTimeLoggingConfig;
import org.springframework.context.annotation.Import;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ExecutionTimeLoggingConfig.class)
public @interface EnableExecutionTimeLogging {
}
