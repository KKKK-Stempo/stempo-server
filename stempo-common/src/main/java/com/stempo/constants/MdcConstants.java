package com.stempo.constants;

public final class MdcConstants {

    public static final String REQUEST_ID = "requestId";
    public static final String TRANSACTION_ID = "transactionId";
    public static final String CLIENT_IP = "clientIp";
    public static final String REQUEST_URL = "requestUrl";
    public static final String HTTP_METHOD = "httpMethod";
    public static final String USER_AGENT = "userAgent";
    public static final String SERVICE_NAME = "serviceName";
    public static final String ENV = "env";
    public static final String USER_ID = "userId";
    public static final String DURATION_MS = "durationMs";
    public static final String HTTP_STATUS = "httpStatus";
    public static final String EXCEPTION_CLASS = "exceptionClass";
    public static final String EXCEPTION_MESSAGE = "exceptionMessage";
    public static final String EXCEPTION_AT = "exceptionAt";
    public static final String ERROR_CODE = "errorCode";
    public static final String SERVICE_EXECUTION_TIME_MS = "serviceExecutionTimeMs";
    public static final String REPOSITORY_EXECUTION_TIME_MS = "repositoryExecutionTimeMs";
    private MdcConstants() {
    }
}
