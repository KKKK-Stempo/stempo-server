package com.stempo.logging.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.MDC;

/**
 * 실행 시간을 측정하여 MDC에 기록하는 공통 로직을 제공하는 추상 클래스입니다. 하위 클래스는 getMdcKey()를 구현하여 자신에게 해당하는 MDC 키를 반환하면 됩니다.
 */
public abstract class AbstractExecutionTimeAspect {

    /**
     * 하위 Aspect에서 MDC에 기록할 키를 반환합니다.
     *
     * @return MDC 키 문자열
     */
    protected abstract String getMdcKey();

    /**
     * 주어진 AOP joinPoint에 대해 실행 시간을 측정하고, 지정된 MDC 키에 실행 시간을 기록합니다.
     *
     * @param joinPoint AOP joinPoint
     * @return 대상 메소드의 실행 결과
     * @throws Throwable 대상 메소드 실행 중 발생한 예외
     */
    protected Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        try {
            return joinPoint.proceed();
        } finally {
            long duration = System.currentTimeMillis() - start;
            MDC.put(getMdcKey(), String.valueOf(duration));
        }
    }
}
