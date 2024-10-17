package com.stempo.exception;

import com.stempo.dto.ApiResponse;
import com.stempo.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.UnknownPathException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.concurrent.CompletionException;

@RestControllerAdvice(basePackages = "com.stempo")
@RequiredArgsConstructor
@Slf4j
public class ApplicationExceptionHandler {

    @ExceptionHandler({
            InvalidPasswordException.class,
            StringIndexOutOfBoundsException.class,
            IllegalAccessException.class,
            NumberFormatException.class,
            UnknownPathException.class,
            NoSuchFieldException.class,
            IllegalArgumentException.class
    })
    public ErrorResponse<Exception> handleBadRequest(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return ErrorResponse.failure(e);
    }

    @ExceptionHandler({
            NullPointerException.class,
            ResourceNotFoundException.class,
    })
    public ErrorResponse<Exception> handleNotFound(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return ErrorResponse.failure(e);
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class
    })
    public ErrorResponse<Exception> handleConflict(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        return ErrorResponse.failure(e);
    }

    @ExceptionHandler(AccountLockedException.class)
    public ErrorResponse<Exception> handleAccountLocked(HttpServletResponse response, AccountLockedException e) {
        response.setStatus(423); // 423 Locked
        return ErrorResponse.failure(e);
    }

    @ExceptionHandler({
            IllegalStateException.class,
            FileUploadFailException.class,
            IncorrectResultSizeDataAccessException.class,
            ArrayIndexOutOfBoundsException.class,
            IOException.class,
            TransactionSystemException.class,
            SecurityException.class,
            CompletionException.class,
            RhythmGenerationException.class,
            Exception.class
    })
    public ApiResponse<Void> handleServerError(HttpServletRequest request, HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return ApiResponse.failure();
    }
}
