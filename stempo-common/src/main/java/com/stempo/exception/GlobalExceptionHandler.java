package com.stempo.exception;

import com.stempo.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.stempo")
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ErrorResponse<Exception> handleBaseException(HttpServletResponse response, BaseException ex) {
        int httpStatus = ex.getErrorCode().getStatus().value();
        response.setStatus(httpStatus);

        MDC.put("exceptionClass", ex.getClass().getName());
        MDC.put("ErrorCode", ex.getErrorCode().name());
        MDC.put("exceptionMessage", ex.getMessage());
        if (ex.getStackTrace().length > 0) {
            MDC.put("exceptionAt", ex.getStackTrace()[0].toString());
        }
        MDC.put("httpStatus", String.valueOf(httpStatus));

        return ErrorResponse.failure(ex);
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse<Exception> handleServerError(HttpServletRequest request, HttpServletResponse response,
        Exception ex) {
        ErrorCode errorCode = ExceptionMapper.getErrorCode(ex);
        int httpStatus = errorCode.getStatus().value();
        response.setStatus(httpStatus);

        MDC.put("exceptionClass", ex.getClass().getName());
        MDC.put("ErrorCode", errorCode.name());
        MDC.put("exceptionMessage", errorCode.getDefaultMessage());
        if (ex.getStackTrace().length > 0) {
            MDC.put("exceptionAt", ex.getStackTrace()[0].toString());
        }
        MDC.put("httpStatus", String.valueOf(httpStatus));

        return ErrorResponse.failure(new BaseException(errorCode, ex.getMessage()));
    }
}
