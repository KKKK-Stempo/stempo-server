package com.stempo.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApiLogger {

    private ApiLogger() {
    }

    public static void logRequest(HttpServletRequest request, HttpServletResponse response,
        String clientIpAddress, String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (authentication == null || authentication.getName() == null)
            ? "anonymous" : authentication.getName();

        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestUrl += "?" + queryString;
        }
        String httpMethod = request.getMethod();
        int httpStatus = response.getStatus();

        String userAgent = request.getHeader("User-Agent");
        String serviceName = "stempo-core";
        String env = System.getProperty("spring.profiles.active", "default");

        MDC.put("clientIp", clientIpAddress);
        MDC.put("userId", userId);
        MDC.put("requestUrl", requestUrl);
        MDC.put("httpMethod", httpMethod);
        MDC.put("httpStatus", String.valueOf(httpStatus));
        if (userAgent != null) {
            MDC.put("userAgent", userAgent);
        }
        MDC.put("serviceName", serviceName);
        MDC.put("env", env);

        log.info(message);

        MDC.remove("clientIp");
        MDC.remove("userId");
        MDC.remove("requestUrl");
        MDC.remove("httpMethod");
        MDC.remove("httpStatus");
        MDC.remove("userAgent");
        MDC.remove("serviceName");
        MDC.remove("env");
    }

    public static void logRequestDuration(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (authentication == null || authentication.getName() == null)
            ? "anonymous" : authentication.getName();

        String clientIpAddress = HttpReqResUtils.getClientIpAddressIfServletRequestExist();
        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestUrl += "?" + queryString;
        }
        String httpMethod = request.getMethod();
        int httpStatus = response.getStatus();

        long startTime = (Long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - startTime;

        String userAgent = request.getHeader("User-Agent");
        String serviceName = "stempo-core";
        String env = System.getProperty("spring.profiles.active", "default");

        MDC.put("clientIp", clientIpAddress);
        MDC.put("userId", userId);
        MDC.put("requestUrl", requestUrl);
        MDC.put("httpMethod", httpMethod);
        MDC.put("httpStatus", String.valueOf(httpStatus));
        MDC.put("durationMs", String.valueOf(duration));
        if (userAgent != null) {
            MDC.put("userAgent", userAgent);
        }
        MDC.put("serviceName", serviceName);
        MDC.put("env", env);

        if (ex == null) {
            log.info("Request completed successfully");
        } else {
            MDC.put("exception", ex.getMessage());
            log.error("Request completed with error");
            MDC.remove("exception");
        }

        MDC.remove("clientIp");
        MDC.remove("userId");
        MDC.remove("requestUrl");
        MDC.remove("httpMethod");
        MDC.remove("httpStatus");
        MDC.remove("durationMs");
        MDC.remove("userAgent");
        MDC.remove("serviceName");
        MDC.remove("env");
    }
}
