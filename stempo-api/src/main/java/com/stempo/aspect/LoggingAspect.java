package com.stempo.aspect;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * 컨트롤러에 속한 모든 요청에 대해 로깅을 수행한다.
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController) || " +
        "@within(org.springframework.stereotype.Controller)")
    public Object logControllerRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        return logRequest(joinPoint, "Controller");
    }

    /**
     * 지정된 대상의 요청 처리 시간 및 HTTP 상태를 MDC에 추가한 후,
     * 정상 요청인 경우에만 로그를 남기고, 예외 발생 시에는 예외를 전파하여 글로벌 예외 핸들러에서 로그가 남도록 한다.
     *
     * @param joinPoint AOP 대상
     * @param type      로그 구분을 위한 문자열 (예: "Controller", "SecurityConfig.handleException")
     * @return 대상 메소드 실행 결과
     * @throws Throwable 예외 발생 시 그대로 전파
     */
    private Object logRequest(ProceedingJoinPoint joinPoint, String type) throws Throwable {
        long startTime = System.currentTimeMillis();
        boolean errorOccurred = false;
        Object result = null;

        // (1) API 요청 시 userId를 MDC에 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (authentication == null || authentication.getName() == null) ? "anonymous" : authentication.getName();
        MDC.put("userId", userId);

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Throwable t) {
            errorOccurred = true;
            throw t;
        } finally {
            // (2) 요청 처리 종료 시 처리 시간 및 httpStatus 설정
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("durationMs", String.valueOf(duration));
            int statusCode = 0;
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes instanceof ServletRequestAttributes sra) {
                HttpServletResponse response = sra.getResponse();
                if (response != null) {
                    statusCode = response.getStatus();
                    MDC.put("httpStatus", String.valueOf(statusCode));
                }
            }
            // (3) 예외가 발생한 경우에는 글로벌 예외 핸들러에서 로그를 남기므로 여기서는 로그를 남기지 않음
            if (!errorOccurred) {
                HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(statusCode);
                if (httpStatusCode.is5xxServerError()) {
                    log.error("{} request completed with server error.", type);
                } else if (httpStatusCode.is4xxClientError()) {
                    log.warn("{} request completed with client error.", type);
                } else if (httpStatusCode.is1xxInformational() || httpStatusCode.is2xxSuccessful() || httpStatusCode.is3xxRedirection()) {
                    log.info("{} request completed successfully.", type);
                } else {
                    log.debug("{} request completed.", type);
                }
            }
        }
    }
}
