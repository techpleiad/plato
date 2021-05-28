package org.techpleiad.plato.core.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Aspect
@Slf4j
@Order(0)
public class ExecutionTimeAspect {

    @Around("@annotation(org.techpleiad.plato.core.advice.ExecutionTime)")
    public Object trackTime(final ProceedingJoinPoint joinPoint) throws Throwable {

        final long startTime = System.currentTimeMillis();
        final Object returnObject = joinPoint.proceed();
        final long endTime = System.currentTimeMillis();

        log.info("{} ms taken by {}", (endTime - startTime), joinPoint.getSignature());

        return returnObject;
    }
}
