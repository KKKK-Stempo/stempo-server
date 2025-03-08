package com.stempo.logging.aspect;

import com.stempo.logging.constants.MdcConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class ServiceLoggingAspect extends AbstractExecutionTimeAspect {

    @Override
    protected String getMdcKey() {
        return MdcConstants.MDC_SERVICE_EXECUTION_TIME_MS.getKey();
    }

    @Around("execution(* com.stempo.service..*(..))")
    public Object logServiceExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint);
    }
}
