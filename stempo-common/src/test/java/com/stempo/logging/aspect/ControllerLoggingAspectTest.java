package com.stempo.logging.aspect;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.stempo.logging.constants.MdcConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

class ControllerLoggingAspectTest {

    private final ControllerLoggingAspect aspect = new ControllerLoggingAspect();

    @AfterEach
    void tearDown() {
        MDC.clear();
        RequestContextHolder.resetRequestAttributes();
        SecurityContextHolder.clearContext();
    }

    @Test
    void 정상_요청일때_HTTP_상태_200이면_MDC에_200이_기록된다() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("result200");
        TestingAuthenticationToken auth = new TestingAuthenticationToken("user200", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getStatus()).thenReturn(HttpStatus.OK.value()); // 200
        HttpServletRequest request = mock(HttpServletRequest.class); // non-null request
        ServletRequestAttributes sra = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(sra);

        // when
        Object result = aspect.logControllerRequest(joinPoint);

        // then
        assertThat(result).isEqualTo("result200");
        assertThat(MDC.get(MdcConstants.MDC_USER_ID.getKey())).isEqualTo("user200");
        assertThat(MDC.get(MdcConstants.MDC_HTTP_STATUS.getKey())).isEqualTo("200");
    }

    @Test
    void 정상_요청일때_HTTP_상태_404이면_MDC에_404가_기록된다() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("result404");
        TestingAuthenticationToken auth = new TestingAuthenticationToken("user404", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getStatus()).thenReturn(HttpStatus.NOT_FOUND.value()); // 404
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletRequestAttributes sra = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(sra);

        // when
        Object result = aspect.logControllerRequest(joinPoint);

        // then
        assertThat(result).isEqualTo("result404");
        assertThat(MDC.get(MdcConstants.MDC_USER_ID.getKey())).isEqualTo("user404");
        assertThat(MDC.get(MdcConstants.MDC_HTTP_STATUS.getKey())).isEqualTo("404");
    }

    @Test
    void 정상_요청일때_HTTP_상태_500이면_MDC에_500이_기록된다() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("result500");
        TestingAuthenticationToken auth = new TestingAuthenticationToken("user500", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value()); // 500
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletRequestAttributes sra = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(sra);

        // when
        Object result = aspect.logControllerRequest(joinPoint);

        // then
        assertThat(result).isEqualTo("result500");
        assertThat(MDC.get(MdcConstants.MDC_USER_ID.getKey())).isEqualTo("user500");
        assertThat(MDC.get(MdcConstants.MDC_HTTP_STATUS.getKey())).isEqualTo("500");
    }

    @Test
    void RequestAttributes가_없으면_HTTP_STATUS설정시_예외가_발생한다() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("resultNoAttributes");
        TestingAuthenticationToken auth = new TestingAuthenticationToken("userNoAttr", null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        // when, then
        assertThatThrownBy(() -> aspect.logControllerRequest(joinPoint))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Status code '0'");
    }

    @Test
    void 인증정보가_없으면_익명으로_설정된다() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        when(joinPoint.proceed()).thenReturn("resultAnonymous");
        SecurityContextHolder.clearContext();
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getStatus()).thenReturn(HttpStatus.OK.value());
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletRequestAttributes sra = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(sra);

        // when
        Object result = aspect.logControllerRequest(joinPoint);

        // then
        assertThat(result).isEqualTo("resultAnonymous");
        assertThat(MDC.get(MdcConstants.MDC_USER_ID.getKey())).isEqualTo("anonymous");
        assertThat(MDC.get(MdcConstants.MDC_HTTP_STATUS.getKey())).isEqualTo("200");
    }

    @Test
    void 예외_발생시_요청_후_MDC설정이_정상적으로_이뤄진다() throws Throwable {
        // given
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        RuntimeException exception = new RuntimeException("Test exception");
        when(joinPoint.proceed()).thenThrow(exception);
        TestingAuthenticationToken auth = new TestingAuthenticationToken("userException", null);
        SecurityContextHolder.getContext().setAuthentication(auth);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(response.getStatus()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR.value());
        HttpServletRequest request = mock(HttpServletRequest.class);
        ServletRequestAttributes sra = new ServletRequestAttributes(request, response);
        RequestContextHolder.setRequestAttributes(sra);

        // when, then
        assertThatThrownBy(() -> aspect.logControllerRequest(joinPoint))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Test exception");
        assertThat(MDC.get(MdcConstants.MDC_USER_ID.getKey())).isEqualTo("userException");
        String durationStr = MDC.get(MdcConstants.MDC_DURATION_MS.getKey());
        assertThat(durationStr).isNotNull();
        assertThat(Long.parseLong(durationStr)).isGreaterThanOrEqualTo(0L);
        assertThat(MDC.get(MdcConstants.MDC_HTTP_STATUS.getKey()))
            .isEqualTo(String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
