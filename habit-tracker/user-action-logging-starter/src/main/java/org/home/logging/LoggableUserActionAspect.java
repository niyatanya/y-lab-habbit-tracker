package org.home.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An aspect that logs user actions for methods or classes annotated with
 * corresponding annotation.
 */
@Aspect
public class LoggableUserActionAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggableUserActionAspect.class);

    /**
     * Pointcut that matches methods or classes annotated with
     * {@link org.home.logging.annotations.LoggableUserAction}.
     */
    @Pointcut("within(@org.home.logging.annotations.LoggableUserAction *)")
    public void annotatedByLoggableUserAction() {}

    /**
     * After advice that logs a message when a user calls a method
     * annotated with {@link org.home.logging.annotations.LoggableUserAction}.
     */
    @After("annotatedByLoggableUserAction()")
    public void logUserAction(JoinPoint joinPoint) {
        logger.info("User calls method: {}.",
                joinPoint.getSignature());
    }
}
