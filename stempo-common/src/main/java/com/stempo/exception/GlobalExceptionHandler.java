package com.stempo.exception;

import com.stempo.dto.ErrorResponse;
import com.stempo.logging.constants.MdcConstants;
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

        MDC.put(MdcConstants.MDC_EXCEPTION_CLASS.getKey(), ex.getClass().getName());
        MDC.put(MdcConstants.MDC_ERROR_CODE.getKey(), ex.getErrorCode().name());
        MDC.put(MdcConstants.MDC_EXCEPTION_MESSAGE.getKey(), ex.getMessage());
        if (ex.getStackTrace().length > 0) {
            MDC.put(MdcConstants.MDC_EXCEPTION_AT.getKey(), ex.getStackTrace()[0].toString());
        }
        MDC.put(MdcConstants.MDC_HTTP_STATUS.getKey(), String.valueOf(httpStatus));

        return ErrorResponse.failure(ex);
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponse<Exception> handleServerError(HttpServletRequest request, HttpServletResponse response,
        Exception ex) {
        ErrorCode errorCode = ExceptionMapper.getErrorCode(ex);
        int httpStatus = errorCode.getStatus().value();
        response.setStatus(httpStatus);

        MDC.put(MdcConstants.MDC_EXCEPTION_CLASS.getKey(), ex.getClass().getName());
        MDC.put(MdcConstants.MDC_ERROR_CODE.getKey(), errorCode.name());
        MDC.put(MdcConstants.MDC_EXCEPTION_MESSAGE.getKey(), errorCode.getDefaultMessage());
        if (ex.getStackTrace().length > 0) {
            MDC.put(MdcConstants.MDC_EXCEPTION_AT.getKey(), ex.getStackTrace()[0].toString());
        }
        MDC.put(MdcConstants.MDC_HTTP_STATUS.getKey(), String.valueOf(httpStatus));

        return ErrorResponse.failure(new BaseException(errorCode, ex.getMessage()));
    }
}
