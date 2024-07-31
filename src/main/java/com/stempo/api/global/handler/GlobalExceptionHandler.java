package com.stempo.api.global.handler;

import com.google.gson.stream.MalformedJsonException;
import com.stempo.api.domain.application.exception.DirectoryCreationException;
import com.stempo.api.domain.application.exception.FileUploadFailException;
import com.stempo.api.domain.application.exception.RhythmGenerationException;
import com.stempo.api.global.auth.exception.AuthenticationInfoNotFoundException;
import com.stempo.api.global.auth.exception.TokenForgeryException;
import com.stempo.api.global.auth.exception.TokenNotFoundException;
import com.stempo.api.global.auth.exception.TokenValidateException;
import com.stempo.api.global.common.dto.ApiResponse;
import com.stempo.api.global.common.dto.ErrorResponse;
import com.stempo.api.global.exception.InvalidColumnException;
import com.stempo.api.global.exception.NotFoundException;
import com.stempo.api.global.exception.PermissionDeniedException;
import com.stempo.api.global.exception.SortingArgumentException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.query.sqm.UnknownPathException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;

@RestControllerAdvice(basePackages = "com.stempo.api")
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            StringIndexOutOfBoundsException.class,
            MissingServletRequestParameterException.class,
            MalformedJsonException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalAccessException.class,
            NumberFormatException.class,
            UnknownPathException.class,
            InvalidColumnException.class,
            SortingArgumentException.class,
            IllegalArgumentException.class
    })
    public ErrorResponse<Exception> badRequestException(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return ErrorResponse.failure(e);
    }

    @ExceptionHandler({
            AuthenticationInfoNotFoundException.class,
            AccessDeniedException.class,
            BadCredentialsException.class,
            TokenValidateException.class,
            TokenNotFoundException.class,
            TokenForgeryException.class,
            io.jsonwebtoken.security.SecurityException.class,
            MalformedJwtException.class,
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
    })
    public ApiResponse<Void> unAuthorizeException(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return ApiResponse.failure();
    }

    @ExceptionHandler({
            PermissionDeniedException.class
    })
    public ApiResponse<Void> deniedException(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return ApiResponse.failure();
    }

    @ExceptionHandler({
            NullPointerException.class,
            NotFoundException.class,
            NoSuchElementException.class,
            FileNotFoundException.class
    })
    public ErrorResponse<Exception> notFoundException(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return ErrorResponse.failure(e);
    }

    @ExceptionHandler({
            IllegalStateException.class,
            FileUploadFailException.class,
            DataIntegrityViolationException.class,
            IncorrectResultSizeDataAccessException.class,
            ArrayIndexOutOfBoundsException.class,
            IOException.class,
            TransactionSystemException.class,
            SecurityException.class,
            CompletionException.class,
            InvalidDataAccessApiUsageException.class,
            DirectoryCreationException.class,
            RhythmGenerationException.class,
            Exception.class
    })
    public ApiResponse<Void> serverException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        log.warn(e.getMessage());
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return ApiResponse.failure();
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class
    })
    public ApiResponse<Void> handleValidationException(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return ApiResponse.failure();
    }
}
