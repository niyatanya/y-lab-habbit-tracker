package org.home.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An aspect that logs the execution time of all methods in the application.
 */
@Aspect
public class AllMethodsExecutionTimeAspect {

    private static final Logger logger = LoggerFactory.getLogger(AllMethodsExecutionTimeAspect.class);

    /**
     * Pointcut that matches the execution of any method in the application.
     */
    @Pointcut("execution(* org.home..* (..)) && !within(org.springframework..*) && !within(org.home.config..*)")
    public void allMethods() { }

    /**
     * Around advice that logs the execution time of the matched methods.
     */
    @Around("allMethods()")
    public Object logging(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long timeNeeded = System.currentTimeMillis() - startTime;
        logger.info("Execution of method {} finished. Execution time is {} ms.",
                joinPoint.getSignature(), timeNeeded);
        return result;
    }
}
