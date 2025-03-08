package com.stempo.logging.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class AbstractExecutionTimeAspectTest {

    private final TestExecutionTimeAspect aspect = new TestExecutionTimeAspect();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void 메소드_실행시간이_MDC에_정상적으로_기록된다() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("successResult");

        // when
        Object result = aspect.logExecutionTime(joinPoint);

        // then
        assertThat(result).isEqualTo("successResult");
        String mdcValue = MDC.get("testKey");
        assertThat(mdcValue).isNotNull();
        long duration = Long.parseLong(mdcValue);
        assertThat(duration).isGreaterThanOrEqualTo(0L);
        verify(joinPoint, times(1)).proceed();
    }

    // 테스트를 위한 하위 클래스 생성
    static class TestExecutionTimeAspect extends AbstractExecutionTimeAspect {

        @Override
        protected String getMdcKey() {
            return "testKey";
        }
    }
}
