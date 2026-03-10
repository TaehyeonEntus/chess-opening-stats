package com.chessopeningstats.backend.util.logger;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ExecutionTimeAspect {

    @Around("@annotation(com.chessopeningstats.backend.util.logger.LogExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {

        long start = System.currentTimeMillis();

        String methodName = joinPoint.getSignature().toShortString();
        log.warn("[START] {}", methodName);

        Object result = joinPoint.proceed();

        long end = System.currentTimeMillis();
        log.warn("[END] {} - {}ms", methodName, (end - start));

        return result;
    }
}
