package org.home.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AllMethodsExecutionTimeAspect {
    @Pointcut("execution(* * (..))")
    public void allMethods() { }

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
