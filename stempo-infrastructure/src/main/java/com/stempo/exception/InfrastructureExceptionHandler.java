package com.stempo.exception;

import com.stempo.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice(basePackages = "com.stempo")
@RequiredArgsConstructor
@Slf4j
public class InfrastructureExceptionHandler {

    @ExceptionHandler({
            InvalidDataAccessApiUsageException.class
    })
    public ErrorResponse<Exception> handleBadRequest(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return ErrorResponse.failure(e);
    }

    @ExceptionHandler({
            SQLException.class,
            DataIntegrityViolationException.class,
            Exception.class
    })
    public ErrorResponse<Exception> handleServerError(HttpServletResponse response, Exception e) {
        log.error("Server Error: {}", e.getMessage());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return ErrorResponse.failure(e);
    }
}
