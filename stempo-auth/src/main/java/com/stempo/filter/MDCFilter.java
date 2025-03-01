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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Slf4j
public class MDCFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        long startTime = System.currentTimeMillis();

        String requestId = wrappedRequest.getHeader("X-Request-Id");
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put("requestId", requestId);

        String transactionId = wrappedRequest.getHeader("X-Transaction-Id");
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
        }
        MDC.put("transactionId", transactionId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = (authentication == null || authentication.getName() == null)
            ? "anonymous" : authentication.getName();
        MDC.put("userId", userId);

        String clientIp = HttpReqResUtils.getClientIpAddressIfServletRequestExist();
        MDC.put("clientIp", clientIp);

        String requestUrl = wrappedRequest.getRequestURI();
        String queryString = wrappedRequest.getQueryString();
        if (queryString != null) {
            requestUrl += "?" + queryString;
        }
        MDC.put("requestUrl", requestUrl);

        MDC.put("httpMethod", wrappedRequest.getMethod());

        String userAgent = wrappedRequest.getHeader("User-Agent");
        if (userAgent != null) {
            MDC.put("userAgent", userAgent);
        }

        MDC.put("serviceName", "stempo-core");
        MDC.put("env", System.getProperty("spring.profiles.active", "default"));

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception ex) {
            MDC.put("exception", ex.getMessage());
            throw ex;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("durationMs", String.valueOf(duration));

            int requestBodySize = wrappedRequest.getContentAsByteArray().length;
            int responseBodySize = wrappedResponse.getContentAsByteArray().length;
            MDC.put("requestBodySize", String.valueOf(requestBodySize));
            MDC.put("responseBodySize", String.valueOf(responseBodySize));

            MDC.put("httpStatus", String.valueOf(wrappedResponse.getStatus()));

            if (MDC.get("exception") != null) {
                log.error("Request completed with error.");
            } else {
                log.info("Request completed successfully.");
            }

            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }
}
