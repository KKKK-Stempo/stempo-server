package com.stempo.config;

import com.stempo.util.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) throws IOException, ServletException {
        // 예외 상황에 따른 메시지와 HTTP 상태 설정
        String message = "인증되지 않은 사용자의 비정상적인 접근이 감지되었습니다.";
        int httpStatus = HttpServletResponse.SC_UNAUTHORIZED;

        // MDC에 예외 관련 정보 기록
        MDC.put("exceptionClass", authException.getClass().getName());
        MDC.put("exceptionMessage", message);
        if (authException.getStackTrace().length > 0) {
            MDC.put("exceptionAt", authException.getStackTrace()[0].toString());
        }
        MDC.put("httpStatus", String.valueOf(httpStatus));

        // 로그 기록
        log.warn(message, authException);

        // 에러 응답 전송
        ResponseUtils.sendErrorResponse(response, httpStatus);
    }
}
