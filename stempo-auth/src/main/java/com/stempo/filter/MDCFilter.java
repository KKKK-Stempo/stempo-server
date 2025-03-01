package com.stempo.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class MDCFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        try {
            String transactionId = httpRequest.getHeader("X-Transaction-Id");
            if (transactionId == null || transactionId.isEmpty()) {
                transactionId = UUID.randomUUID().toString();
            }
            MDC.put("transactionId", transactionId);

            String requestId = httpRequest.getHeader("X-Request-Id");
            if (requestId == null || requestId.isEmpty()) {
                requestId = UUID.randomUUID().toString();
            }
            MDC.put("requestId", requestId);

            chain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }
}
