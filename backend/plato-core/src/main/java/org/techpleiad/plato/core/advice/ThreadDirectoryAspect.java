package org.techpleiad.plato.core.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.techpleiad.plato.core.port.in.IFileThreadServiceUseCase;
import org.techpleiad.plato.core.port.out.IGetWorkingDirectoryPort;

import java.util.UUID;

@Component
@Aspect
@Slf4j
@Order(1)
public class ThreadDirectoryAspect {

    @Autowired
    private IGetWorkingDirectoryPort getWorkingDirectoryPort;

    @Autowired
    private IFileThreadServiceUseCase fileThreadServiceUseCase;

    @Around("@annotation(org.techpleiad.plato.core.advice.ThreadDirectory)")
    public Object trackTime(final ProceedingJoinPoint joinPoint) throws Throwable {

        final String directory = getWorkingDirectoryPort.getRootWorkingDirectory() + "/" + UUID.randomUUID().toString();
        try {
            fileThreadServiceUseCase.createThreadRootDirectory(directory);
            final Object returnObject = joinPoint.proceed();
            return returnObject;
        } finally {
            fileThreadServiceUseCase.deleteThreadRootDirectory();
        }
    }
}
