package org.home.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * An aspect that logs the execution time of all methods in the application.
 */
@Aspect
@Component
public class AllMethodsExecutionTimeAspect {

    /**
     * Pointcut that matches the execution of any method in the application.
     */
    @Pointcut("execution(* * (..))")
    public void allMethods() { }

    /**
     * Around advice that logs the execution time of the matched methods.
     */
    @Around("allMethods()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long timeNeeded = System.currentTimeMillis() - startTime;
        System.out.println("Execution of method " + joinPoint.getSignature()
                + "finished. Execution time is " + timeNeeded + " ms.");
        return result;
    }
}
