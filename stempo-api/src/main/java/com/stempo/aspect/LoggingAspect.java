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

    @Around("@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)")
    public Object logControllerRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        boolean errorOccurred = false;

        // (1) API 요청 시 userId를 MDC에 설정 (AOP 레벨)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId =
            (authentication == null || authentication.getName() == null) ? "anonymous" : authentication.getName();
        MDC.put("userId", userId);

        try {
            return joinPoint.proceed();
        } catch (Throwable t) {
            errorOccurred = true;
            throw t;
        } finally {
            // (2) 요청 종료 시 처리 시간 및 httpStatus 설정
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

            // (3) 예외가 발생한 경우는 글로벌 예외 핸들러가 로그를 남기므로 여기서는 로그를 남기지 않음
            if (!errorOccurred) {
                HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(statusCode);
                if (httpStatusCode.is5xxServerError()) {
                    log.error("Request completed with server error.");
                } else if (httpStatusCode.is4xxClientError()) {
                    log.warn("Request completed with client error.");
                } else if (httpStatusCode.is1xxInformational() || httpStatusCode.is2xxSuccessful()
                    || httpStatusCode.is3xxRedirection()) {
                    log.info("Request completed successfully.");
                } else {
                    log.debug("Request completed.");
                }
            }
        }
    }
}
