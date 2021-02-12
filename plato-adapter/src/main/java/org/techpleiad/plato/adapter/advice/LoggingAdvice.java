package org.techpleiad.plato.adapter.advice;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.techpleiad.plato.adapter.exception.ErrorResponse;

@Component
@Aspect
@Slf4j
public class LoggingAdvice {

    @Autowired
    ObjectMapper mapper;

    @Pointcut(value = "within(org.techpleiad.plato.adapter.web.in.ServiceManagerController)")
    private void serviceControllerPointCut() {
        /* No return */
    }

    @Pointcut(value = "execution(* org.techpleiad.plato.adapter.advice.WebControllerAdvice.*(..))")
    private void exceptionLogPointCut() {
        /* No return */
    }

    //    @Around(value = "serviceControllerPointCut()")
    private Object logger(final ProceedingJoinPoint joinPoint) throws Throwable {

        final String className = joinPoint.getTarget().getClass().getName();
        final String methodName = joinPoint.getSignature().getName();

        log.info("BeforeAdvice : {}.{}\n{}", className, methodName,
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(joinPoint.getArgs()));

        final Object returnObject = joinPoint.proceed();

        log.info("AfterAdvice : {}.{}\nResponse:\n{}", className, methodName,
                mapper.writerWithDefaultPrettyPrinter().writeValueAsString(returnObject));

        return returnObject;
    }

    @Around(value = "exceptionLogPointCut() && args(exception)")
    private Object exceptionLogger(final ProceedingJoinPoint joinPoint, final Exception exception) throws Throwable {

        final String className = exception.getClass().getSimpleName();
        log.info("Exception :: {}, value :: {}", className, mapper.writeValueAsString(joinPoint.getArgs()));

        final Object object = joinPoint.proceed();

        final ErrorResponse errorResponse = ((ResponseEntity<ErrorResponse>) object).getBody();
        log.info("\nErrorResponse :: {}", mapper.writerWithDefaultPrettyPrinter()
                .writeValueAsString(errorResponse));

        return object;
    }

}
