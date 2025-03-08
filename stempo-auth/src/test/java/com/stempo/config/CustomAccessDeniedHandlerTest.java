package com.stempo.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mockStatic;

import com.stempo.logging.constants.MdcConstants;
import com.stempo.util.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.slf4j.MDC;
import org.springframework.security.access.AccessDeniedException;

class CustomAccessDeniedHandlerTest {

    private final CustomAccessDeniedHandler handler = new CustomAccessDeniedHandler();

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void 정상_액세스_거부시_MDC및응답_설정이_정상적으로_이뤄진다() throws IOException, ServletException {
        // given
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = org.mockito.Mockito.mock(HttpServletResponse.class);
        AccessDeniedException exception = new AccessDeniedException("Access Denied");
        // 스택트레이스가 비어있지 않도록 설정
        if (exception.getStackTrace().length == 0) {
            exception.setStackTrace(new StackTraceElement[]{
                new StackTraceElement("TestClass", "testMethod", "TestFile.java", 123)
            });
        }
        int expectedStatus = HttpServletResponse.SC_FORBIDDEN;
        String expectedMessage = "권한이 없는 엔드포인트에 대한 접근이 감지되었습니다.";

        try (MockedStatic<ResponseUtils> mockedStatic = mockStatic(ResponseUtils.class)) {
            // when
            handler.handle(request, response, exception);

            // then
            mockedStatic.verify(() -> ResponseUtils.sendErrorResponse(response, expectedStatus));
        }
        // then: MDC 값 검증
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_CLASS.getKey())).isEqualTo(exception.getClass().getName());
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_MESSAGE.getKey())).isEqualTo(expectedMessage);
        assertThat(MDC.get(MdcConstants.MDC_HTTP_STATUS.getKey())).isEqualTo(String.valueOf(expectedStatus));
        // 스택트레이스가 있으므로 첫 번째 요소가 MDC_EXCEPTION_AT에 기록되어야 함
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_AT.getKey()))
            .isEqualTo(exception.getStackTrace()[0].toString());
    }

    @Test
    void 스택트레이스가_비어있으면_MDC_EXCEPTION_AT가_설정되지_않는다() throws IOException, ServletException {
        // given
        HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);
        HttpServletResponse response = org.mockito.Mockito.mock(HttpServletResponse.class);
        AccessDeniedException exception = new AccessDeniedException("Access Denied");
        // 스택트레이스를 비어있는 배열로 설정
        exception.setStackTrace(new StackTraceElement[0]);
        int expectedStatus = HttpServletResponse.SC_FORBIDDEN;

        try (MockedStatic<ResponseUtils> mockedStatic = mockStatic(ResponseUtils.class)) {
            // when
            handler.handle(request, response, exception);

            // then
            mockedStatic.verify(() -> ResponseUtils.sendErrorResponse(response, expectedStatus));
        }
        // then: MDC 값 검증 (MDC_EXCEPTION_AT는 설정되지 않아야 함)
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_CLASS.getKey())).isEqualTo(exception.getClass().getName());
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_MESSAGE.getKey()))
            .isEqualTo("권한이 없는 엔드포인트에 대한 접근이 감지되었습니다.");
        assertThat(MDC.get(MdcConstants.MDC_HTTP_STATUS.getKey())).isEqualTo(String.valueOf(expectedStatus));
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_AT.getKey())).isNull();
    }
}
