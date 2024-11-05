package org.home.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * An aspect that logs user actions for methods or classes annotated with
 * corresponding annotation.
 */
@Aspect
@Component
public class LoggableUserActionAspect {

    /**
     * Pointcut that matches methods or classes annotated with
     * {@link org.home.annotations.LoggableUserAction}.
     */
    @Pointcut("within(@org.home.annotations.LoggableUserAction *)")
    public void annotatedByLoggableUserAction() { }

    /**
     * After advice that logs a message when a user calls a method
     * annotated with {@link org.home.annotations.LoggableUserAction}.
     */
    @After("annotatedByLoggableUserAction()")
    public void logging(JoinPoint joinPoint) {
        System.out.println("User calls method: " + joinPoint.getSignature());
    }
}
