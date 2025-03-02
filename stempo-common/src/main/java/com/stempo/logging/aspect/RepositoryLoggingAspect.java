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
public class RepositoryLoggingAspect extends AbstractExecutionTimeAspect {

    @Override
    protected String getMdcKey() {
        return MdcConstants.REPOSITORY_EXECUTION_TIME_MS;
    }

    @Around("execution(* com.stempo.repository..*(..))")
    public Object logRepositoryExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        return logExecutionTime(joinPoint);
    }
}
