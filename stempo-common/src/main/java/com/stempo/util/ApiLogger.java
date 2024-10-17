package com.stempo.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ApiLogger {

    public static void logRequest(HttpServletRequest request, HttpServletResponse response, String clientIpAddress, String message) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = (authentication == null || authentication.getName() == null) ? "anonymous" : authentication.getName();

        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = queryString == null ? requestUrl : requestUrl + "?" + queryString;

        String httpMethod = request.getMethod();
        int httpStatus = response.getStatus();

        log.info("[{}:{}] {} {} {} {}", clientIpAddress, id, fullUrl, httpMethod, httpStatus, message);
    }

    public static void logRequestDuration(HttpServletRequest request, HttpServletResponse response, Exception ex) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String id = (authentication == null || authentication.getName() == null) ? "anonymous" : authentication.getName();
        String clientIpAddress = HttpReqResUtils.getClientIpAddressIfServletRequestExist();

        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        String fullUrl = queryString == null ? requestUrl : requestUrl + "?" + queryString;

        String httpMethod = request.getMethod();
        int httpStatus = response.getStatus();

        long startTime = (Long) request.getAttribute("startTime");
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        if (ex == null) {
            log.info("[{}:{}] {} {} {} {}ms", clientIpAddress, id, fullUrl, httpMethod, httpStatus, duration);
        } else {
            log.error("[{}:{}] {} {} {} {}ms, Exception: {}", clientIpAddress, id, fullUrl, httpMethod, httpStatus, duration, ex.getMessage());
        }
    }
}
