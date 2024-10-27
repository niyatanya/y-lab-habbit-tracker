package org.home.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class LoggableUserActionAspect {
    @Pointcut("within(@org.home.annotations.LoggableUserAction *)")
    public void annotatedByLoggableUserAction() { }

    @After("annotatedByLoggableUserAction()")
    public void logging(ProceedingJoinPoint joinPoint) {
        System.out.println("User calls method: " + joinPoint.getSignature());
    }
}
