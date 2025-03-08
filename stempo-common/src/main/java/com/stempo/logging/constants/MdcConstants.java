package com.stempo.logging.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * MDC 및 HTTP 헤더에 사용되는 상수를 정의한 enum입니다. 각 상수는 로그 추적과 관련된 정보(요청 ID, 트랜잭션 ID, 클라이언트 IP 등)를 관리하는 데 사용됩니다. HTTP 헤더 관련 상수는
 * HEADER_ 접두사를, 로그 MDC 관련 상수는 MDC_ 접두사를 사용합니다.
 */
@Getter
@AllArgsConstructor
public enum MdcConstants {

    // HTTP 헤더 관련 상수
    HEADER_REQUEST_ID("X-Request-Id", "HTTP 요청 헤더의 요청 ID"),
    HEADER_TRANSACTION_ID("X-Transaction-Id", "HTTP 요청 헤더의 트랜잭션 ID"),
    HEADER_USER_AGENT("User-Agent", "HTTP 요청 헤더의 사용자 에이전트"),
    HEADER_CLIENT_IP("X-Client-Ip", "HTTP 요청 헤더의 클라이언트 IP"),

    // 로그 MDC 관련 상수
    MDC_REQUEST_ID("requestId", "로그 MDC에서 요청 ID를 나타내는 키"),
    MDC_TRANSACTION_ID("transactionId", "로그 MDC에서 트랜잭션 ID를 나타내는 키"),
    MDC_CLIENT_IP("clientIp", "로그 MDC에서 클라이언트 IP를 나타내는 키"),
    MDC_REQUEST_URL("requestUrl", "로그 MDC에서 요청 URL을 나타내는 키"),
    MDC_HTTP_METHOD("httpMethod", "로그 MDC에서 HTTP 메소드를 나타내는 키"),
    MDC_USER_AGENT("userAgent", "로그 MDC에서 사용자 에이전트를 나타내는 키"),
    MDC_SERVICE_NAME("serviceName", "로그 MDC에서 서비스 이름을 나타내는 키"),
    MDC_ENV("env", "로그 MDC에서 환경 정보를 나타내는 키"),
    MDC_USER_ID("userId", "로그 MDC에서 사용자 ID를 나타내는 키"),
    MDC_DURATION_MS("durationMs", "로그 MDC에서 처리 시간을 밀리초 단위로 나타내는 키"),
    MDC_HTTP_STATUS("httpStatus", "로그 MDC에서 HTTP 상태 코드를 나타내는 키"),
    MDC_EXCEPTION_CLASS("exceptionClass", "로그 MDC에서 예외 클래스명을 나타내는 키"),
    MDC_EXCEPTION_MESSAGE("exceptionMessage", "로그 MDC에서 예외 메시지를 나타내는 키"),
    MDC_EXCEPTION_AT("exceptionAt", "로그 MDC에서 예외 발생 위치를 나타내는 키"),
    MDC_ERROR_CODE("errorCode", "로그 MDC에서 오류 코드를 나타내는 키"),
    MDC_SERVICE_EXECUTION_TIME_MS("serviceExecutionTimeMs", "로그 MDC에서 서비스 실행 시간을 밀리초 단위로 나타내는 키"),
    MDC_REPOSITORY_EXECUTION_TIME_MS("repositoryExecutionTimeMs", "로그 MDC에서 리포지토리 실행 시간을 밀리초 단위로 나타내는 키");

    private final String key;
    private final String description;
}
