package com.stempo.logging.filter;

import com.stempo.logging.constants.MdcConstants;
import com.stempo.util.HttpReqResUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Component
@Slf4j
public class MDCFilter extends OncePerRequestFilter {

    private static final String HEADER_REQUEST_ID = "X-Request-Id";
    private static final String HEADER_TRANSACTION_ID = "X-Transaction-Id";
    private static final String HEADER_USER_AGENT = "User-Agent";
    private static final String SERVICE_NAME = "stempo-core";
    private static final String ENV_PROPERTY = "spring.profiles.active";
    private static final String ENV_PROPERTY_DEFAULT = "default";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
        throws ServletException, IOException {

        /*
         * ContentCachingRequestWrapper와 ContentCachingResponseWrapper를 사용하는 이유:
         * - 요청 및 응답의 본문 데이터를 캐싱하여, 스트림을 여러 번 읽을 수 있도록 합니다.
         * - 한 번 읽은 후 소진되는 문제를 방지해, 이후 필터나 컨트롤러에서도 본문 데이터에 접근할 수 있습니다.
         * - 향후 로깅, 검증, 디버깅 등 다양한 목적을 위해 본문 데이터를 활용할 수 있도록 확장성을 고려한 구현입니다.
         * 현재는 본문 데이터를 직접 사용하지 않지만, 미래의 요구사항 변화에 대비하여 적용해두었습니다.
         */
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // 초기 MDC 정보 설정
        setInitialMDC(wrappedRequest);

        try {
            chain.doFilter(wrappedRequest, wrappedResponse);
        } catch (Exception ex) {
            // 예외 발생 시 기본 MDC 정보는 그대로 두고, 글로벌 예외 핸들러에서 추가 MDC를 설정하도록 함
            MDC.put(MdcConstants.EXCEPTION_CLASS, ex.getClass().getName());
            MDC.put(MdcConstants.EXCEPTION_MESSAGE, ex.getMessage());
            if (ex.getStackTrace().length > 0) {
                MDC.put(MdcConstants.EXCEPTION_AT, ex.getStackTrace()[0].toString());
            }
            throw ex;
        } finally {
            // 응답 본문을 클라이언트로 전송한 후 MDC 정리
            wrappedResponse.copyBodyToResponse();
            MDC.clear();
        }
    }

    // 요청 초기 MDC 정보를 설정 (필터 레벨)
    private void setInitialMDC(ContentCachingRequestWrapper request) {
        // 요청 ID 설정 (없으면 생성)
        String requestId = request.getHeader(HEADER_REQUEST_ID);
        if (requestId == null || requestId.isEmpty()) {
            requestId = UUID.randomUUID().toString();
        }
        MDC.put(MdcConstants.REQUEST_ID, requestId);

        // 트랜잭션 ID 설정 (없으면 생성)
        String transactionId = request.getHeader(HEADER_TRANSACTION_ID);
        if (transactionId == null || transactionId.isEmpty()) {
            transactionId = UUID.randomUUID().toString();
        }
        MDC.put(MdcConstants.TRANSACTION_ID, transactionId);

        // 클라이언트 IP 설정
        MDC.put(MdcConstants.CLIENT_IP, HttpReqResUtils.getClientIpAddressIfServletRequestExist());

        // 요청 URL 설정 (쿼리스트링 포함)
        String requestUrl = request.getRequestURI();
        String queryString = request.getQueryString();
        if (queryString != null) {
            requestUrl += "?" + queryString;
        }
        MDC.put(MdcConstants.REQUEST_URL, requestUrl);

        // HTTP 메소드 설정
        MDC.put(MdcConstants.HTTP_METHOD, request.getMethod());

        // User-Agent 설정
        String userAgent = request.getHeader(HEADER_USER_AGENT);
        if (userAgent != null) {
            MDC.put(MdcConstants.USER_AGENT, userAgent);
        }

        // 서비스 이름 설정
        MDC.put(MdcConstants.SERVICE_NAME, SERVICE_NAME);

        // 환경 설정
        MDC.put(MdcConstants.ENV, System.getProperty(ENV_PROPERTY, ENV_PROPERTY_DEFAULT));
    }
}
