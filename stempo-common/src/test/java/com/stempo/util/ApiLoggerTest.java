package com.stempo.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class ApiLoggerTest {

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private Authentication mockAuthentication;

    @Mock
    private SecurityContext mockSecurityContext;

    @Mock
    private Appender<ILoggingEvent> mockAppender;

    @BeforeEach
    void setUp() {
        when(mockRequest.getRequestURI()).thenReturn("/test/uri");
        when(mockRequest.getQueryString()).thenReturn("query=1");
        when(mockRequest.getMethod()).thenReturn("GET");
        when(mockResponse.getStatus()).thenReturn(200);
        when(mockSecurityContext.getAuthentication()).thenReturn(mockAuthentication);
        SecurityContextHolder.setContext(mockSecurityContext);

        Logger logger = (Logger) LoggerFactory.getLogger(ApiLogger.class);
        logger.addAppender(mockAppender);
    }

    @Test
    void 정상적으로_로그가_출력되는지_확인한다() {
        // given
        String clientIpAddress = "127.0.0.1";
        String message = "Test message";

        when(mockAuthentication.getName()).thenReturn("user123");

        // when
        ApiLogger.logRequest(mockRequest, mockResponse, clientIpAddress, message);

        // then
        ArgumentCaptor<ILoggingEvent> loggingEventCaptor = ArgumentCaptor.forClass(ILoggingEvent.class);
        verify(mockAppender, times(1)).doAppend(loggingEventCaptor.capture());
        ILoggingEvent capturedLog = loggingEventCaptor.getValue();

        assertThat(capturedLog.getFormattedMessage()).contains(
                "[127.0.0.1:user123] /test/uri?query=1 GET 200 Test message");
    }

    @Test
    void 정상_요청_시간을_로그로_출력한다() {
        // given
        long startTime = 1_000L;
        long endTime = startTime + 500;

        when(mockRequest.getAttribute("startTime")).thenReturn(startTime);

        try (MockedStatic<HttpReqResUtils> mockedHttpUtils = Mockito.mockStatic(HttpReqResUtils.class);
                MockedStatic<TimeUtils> mockedTimeUtils = Mockito.mockStatic(TimeUtils.class)) {

            mockedHttpUtils.when(HttpReqResUtils::getClientIpAddressIfServletRequestExist).thenReturn("127.0.0.1");
            mockedTimeUtils.when(TimeUtils::currentTimeMillis).thenReturn(endTime);

            Exception ex = null;

            // when
            ApiLogger.logRequestDuration(mockRequest, mockResponse, ex);

            // then
            ArgumentCaptor<ILoggingEvent> loggingEventCaptor = ArgumentCaptor.forClass(ILoggingEvent.class);
            verify(mockAppender, times(1)).doAppend(loggingEventCaptor.capture());
            ILoggingEvent capturedLog = loggingEventCaptor.getValue();

            assertThat(capturedLog.getFormattedMessage()).contains(
                    "[127.0.0.1:anonymous] /test/uri?query=1 GET 200 500ms");
        }
    }

    @Test
    void 예외가_있을_때_에러_로그가_출력된다() {
        // given
        long startTime = 1_000L;
        long endTime = startTime + 500;

        when(mockRequest.getAttribute("startTime")).thenReturn(startTime);

        try (MockedStatic<HttpReqResUtils> mockedHttpUtils = Mockito.mockStatic(HttpReqResUtils.class);
                MockedStatic<TimeUtils> mockedTimeUtils = Mockito.mockStatic(TimeUtils.class)) {

            mockedHttpUtils.when(HttpReqResUtils::getClientIpAddressIfServletRequestExist).thenReturn("127.0.0.1");
            mockedTimeUtils.when(TimeUtils::currentTimeMillis).thenReturn(endTime);

            Exception ex = new RuntimeException("Test exception");

            // when
            ApiLogger.logRequestDuration(mockRequest, mockResponse, ex);

            // then
            ArgumentCaptor<ILoggingEvent> loggingEventCaptor = ArgumentCaptor.forClass(ILoggingEvent.class);
            verify(mockAppender, times(1)).doAppend(loggingEventCaptor.capture());
            ILoggingEvent capturedLog = loggingEventCaptor.getValue();

            assertThat(capturedLog.getFormattedMessage()).contains(
                    "[127.0.0.1:anonymous] /test/uri?query=1 GET 200 500ms, Exception: Test exception");
        }
    }
}
