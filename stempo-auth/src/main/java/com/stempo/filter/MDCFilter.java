package com.stempo.filter;

import com.stempo.util.HttpReqResUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Slf4j
public class MDCFilter extends OncePerRequestFilter {

    private static final String HEADER_REQUEST_ID = "X-Request-Id";
    private static final String HEADER_TRANSACTION_ID = "X-Transaction-Id";
    private static final String SERVICE_NAME = "stempo-core";
    private static final String ENV_PROPERTY = "spring.profiles.active";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        // 요청/응답 본문을 읽기 위해 ContentCachingWrapper 사용
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // 요청 처리 시작 시간
        long startTime = System.currentTimeMillis();

        try {
            // 초기 MDC 정보 설정
            setInitialMDC(wrappedRequest);
            chain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception ex) {
            // 예외 발생 시 추가 MDC 정보 설정
            MDC.put("exceptionClass", ex.getClass().getName());
            MDC.put("exceptionMessage", ex.getMessage());
            StackTraceElement[] stackTrace = ex.getStackTrace();
            if (stackTrace.length > 0) {
                MDC.put("exceptionAt", stackTrace[0].toString());
            }
            throw ex;
        } finally {
            // 응답 완료 후 추가 MDC 정보 설정
            setFinalMDC(wrappedRequest, wrappedResponse, startTime);

            // HTTP 상태 코드에 따른 로깅
            logRequest();

            // 응답 본문을 클라이언트로 전송
            wrappedResponse.copyBodyToResponse();

            // MDC 정보 초기화
            MDC.clear();
        }
    }

    private void setInitialMDC(ContentCachingRequestWrapper request) {
        // 요청 ID 설정
        String requestId = request.getHeader(HEADER_REQUEST_ID);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put("requestId", requestId);

        // 트랜잭션 ID 설정
        String transactionId = request.getHeader(HEADER_TRANSACTION_ID);
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
        }
        MDC.put("transactionId", transactionId);

        // 클라이언트 IP 설정
        String clientIp = HttpReqResUtils.getClientIpAddressIfServletRequestExist();
        MDC.put("clientIp", clientIp);

        // 요청 URL 설정
        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestUrl += "?" + queryString;
        }
        MDC.put("requestUrl", requestUrl);

        // HTTP 메소드 설정
        MDC.put("httpMethod", request.getMethod());

        // User-Agent 설정
        String userAgent = request.getHeader("User-Agent");
        if (userAgent != null) {
            MDC.put("userAgent", userAgent);
        }

        // 서비스 이름 설정
        MDC.put("serviceName", SERVICE_NAME);

        // 환경 설정
        MDC.put("env", System.getProperty(ENV_PROPERTY, "default"));
    }

    private void setFinalMDC(ContentCachingRequestWrapper request, ContentCachingResponseWrapper response,
        long startTime) {
        // 인증 정보 (사용자 ID) 설정
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId =
            (authentication == null || authentication.getName() == null) ? "anonymous" : authentication.getName();
        MDC.put("userId", userId);

        // 요청 처리 시간 계산
        long duration = System.currentTimeMillis() - startTime;
        MDC.put("durationMs", String.valueOf(duration));

        // 요청/응답 본문 크기 설정
        int requestBodySize = request.getContentAsByteArray().length;
        int responseBodySize = response.getContentAsByteArray().length;
        MDC.put("requestBodySize", String.valueOf(requestBodySize));
        MDC.put("responseBodySize", String.valueOf(responseBodySize));

        // HTTP 응답 상태 코드 설정
        MDC.put("httpStatus", String.valueOf(response.getStatus()));
    }

    private void logRequest() {
        // HTTP 상태 코드 매핑
        String httpStatusStr = MDC.get("httpStatus");
        int statusCode;
        try {
            statusCode = Integer.parseInt(httpStatusStr);
        } catch (NumberFormatException e) {
            log.error("Unable to parse HTTP status from MDC: {}", httpStatusStr);
            return;
        }

        HttpStatusCode httpStatusCode = HttpStatusCode.valueOf(statusCode);

        // HTTP 상태 코드에 따른 로깅
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
