package com.stempo.logging.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.stempo.logging.constants.MdcConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

class RepositoryLoggingAspectTest {

    private final RepositoryLoggingAspect aspect = new RepositoryLoggingAspect();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void 정상_실행시_저장소_실행시간이_MDC에_기록된다() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("successResult");

        // when
        Object result = aspect.logRepositoryExecutionTime(joinPoint);

        // then
        assertThat(result).isEqualTo("successResult");
        String mdcValue = MDC.get(MdcConstants.MDC_REPOSITORY_EXECUTION_TIME_MS.getKey());
        assertThat(mdcValue).isNotNull();
        long duration = Long.parseLong(mdcValue);
        assertThat(duration).isGreaterThanOrEqualTo(0L);
        verify(joinPoint, times(1)).proceed();
    }

    @Test
    void 예외_발생시_저장소_실행시간이_MDC에_기록된다() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        RuntimeException exception = new RuntimeException("Test Exception");
        when(joinPoint.proceed()).thenThrow(exception);

        // when, then
        assertThatThrownBy(() -> aspect.logRepositoryExecutionTime(joinPoint))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Test Exception");

        String mdcValue = MDC.get(MdcConstants.MDC_REPOSITORY_EXECUTION_TIME_MS.getKey());
        assertThat(mdcValue).isNotNull();
        long duration = Long.parseLong(mdcValue);
        assertThat(duration).isGreaterThanOrEqualTo(0L);
    }
}
