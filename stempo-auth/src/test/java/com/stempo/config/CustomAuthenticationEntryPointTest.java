package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.stempo.logging.constants.MdcConstants;
import com.stempo.util.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.springframework.security.core.AuthenticationException;

class CustomAuthenticationEntryPointTest {

    private final CustomAuthenticationEntryPoint entryPoint = new CustomAuthenticationEntryPoint();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void 인증실패시_MDC및응답설정이_정상적으로_이뤄진다() throws IOException, ServletException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = new AuthenticationException("Authentication failed") {
        };
        // 스택 트레이스가 존재하도록 설정
        authException.setStackTrace(new StackTraceElement[]{
            new StackTraceElement("TestClass", "testMethod", "TestFile.java", 123)
        });
        int expectedStatus = HttpServletResponse.SC_UNAUTHORIZED;
        String expectedMessage = "인증되지 않은 사용자의 비정상적인 접근이 감지되었습니다.";

        try (MockedStatic<ResponseUtils> mockedStatic = Mockito.mockStatic(ResponseUtils.class)) {
            // when
            entryPoint.commence(request, response, authException);
            // then
            mockedStatic.verify(() -> ResponseUtils.sendErrorResponse(response, expectedStatus), times(1));
        }
        // then
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_CLASS.getKey()))
            .isEqualTo(authException.getClass().getName());
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_MESSAGE.getKey()))
            .isEqualTo(expectedMessage);
        assertThat(MDC.get(MdcConstants.MDC_HTTP_STATUS.getKey()))
            .isEqualTo(String.valueOf(expectedStatus));
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_AT.getKey()))
            .isEqualTo(authException.getStackTrace()[0].toString());
    }

    @Test
    void 스택트레이스가_없으면_MDC_EXCEPTION_AT가_설정되지_않는다() throws IOException, ServletException {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = new AuthenticationException("Authentication failed") {
        };
        // 스택 트레이스를 빈 배열로 설정
        authException.setStackTrace(new StackTraceElement[0]);
        int expectedStatus = HttpServletResponse.SC_UNAUTHORIZED;

        try (MockedStatic<ResponseUtils> mockedStatic = Mockito.mockStatic(ResponseUtils.class)) {
            // when
            entryPoint.commence(request, response, authException);
            // then
            mockedStatic.verify(() -> ResponseUtils.sendErrorResponse(response, expectedStatus), times(1));
        }
        // then
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_CLASS.getKey()))
            .isEqualTo(authException.getClass().getName());
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_MESSAGE.getKey()))
            .isEqualTo("인증되지 않은 사용자의 비정상적인 접근이 감지되었습니다.");
        assertThat(MDC.get(MdcConstants.MDC_HTTP_STATUS.getKey()))
            .isEqualTo(String.valueOf(expectedStatus));
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_AT.getKey())).isNull();
    }
}
