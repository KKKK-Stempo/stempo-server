package com.stempo.config;

import com.stempo.util.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 예외 상황에 따른 메시지와 HTTP 상태 설정
        String message = "권한이 없는 엔드포인트에 대한 접근이 감지되었습니다.";
        int httpStatus = HttpServletResponse.SC_FORBIDDEN;

        // MDC에 예외 관련 정보 기록
        MDC.put("exceptionClass", accessDeniedException.getClass().getName());
        MDC.put("exceptionMessage", message);
        if (accessDeniedException.getStackTrace().length > 0) {
            MDC.put("exceptionAt", accessDeniedException.getStackTrace()[0].toString());
        }
        MDC.put("httpStatus", String.valueOf(httpStatus));

        // 로그 기록
        log.warn(accessDeniedException.getMessage());

        // 에러 응답 전송
        ResponseUtils.sendErrorResponse(response, httpStatus);
    }
}
