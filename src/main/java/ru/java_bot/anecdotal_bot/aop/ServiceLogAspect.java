package ru.java_bot.anecdotal_bot.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@Aspect
public class ServiceLogAspect {

    @Pointcut("execution(* ru.java_bot.anecdotal_bot.service..*.*(..))")
    public void callService() {}

    @Before("callService()")
    public void beforeCallService(JoinPoint joinPoint) {
        List<String> args = Arrays.stream(joinPoint.getArgs())
                .map(Object::toString)
                .toList();

        log.info("Service call {} with args {}", joinPoint.getSignature().getName(), args);
    }

    @AfterReturning(value = "callService()", returning = "result")
    public void afterCallService(JoinPoint joinPoint, Object result) {
        log.info("Service call {} returned {}", joinPoint.getSignature().getName(), result);
    }
}
