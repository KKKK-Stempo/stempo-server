package com.stempo.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;

import com.stempo.util.ApiLogger;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class ApiLoggingInterceptorTest {

    @InjectMocks
    private ApiLoggingInterceptor interceptor;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Object handler;

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;

    @BeforeEach
    void setUp() {
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
    }

    @Test
    void 요청이_들어오면_시작_시간을_저장한다() {
        // given
        long startTime = System.currentTimeMillis();
        mockRequest.setAttribute("startTime", startTime);

        // when
        boolean result = interceptor.preHandle(mockRequest, mockResponse, handler);

        // then
        assertThat(result).isTrue();
        assertThat(mockRequest.getAttribute("startTime")).isNotNull();
    }

    @Test
    void 요청이_완료되면_로그를_출력한다() {
        // given
        mockRequest.setAttribute("startTime", System.currentTimeMillis());

        try (MockedStatic<ApiLogger> mockedApiLogger = mockStatic(ApiLogger.class)) {
            // when
            interceptor.afterCompletion(mockRequest, mockResponse, handler, null);

            // then
            mockedApiLogger.verify(() -> ApiLogger.logRequestDuration(mockRequest, mockResponse, null), times(1));
        }
    }

    @Test
    void 요청에서_예외가_발생하면_로그를_출력한다() {
        // given
        mockRequest.setAttribute("startTime", System.currentTimeMillis());
        Exception exception = new RuntimeException("Test Exception");

        try (MockedStatic<ApiLogger> mockedApiLogger = mockStatic(ApiLogger.class)) {
            // when
            interceptor.afterCompletion(mockRequest, mockResponse, handler, exception);

            // then
            mockedApiLogger.verify(() -> ApiLogger.logRequestDuration(mockRequest, mockResponse, exception), times(1));
        }
    }
}
