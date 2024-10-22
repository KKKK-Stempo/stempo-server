package com.stempo.exception;

import com.google.gson.stream.MalformedJsonException;
import com.stempo.dto.ApiResponse;
import com.stempo.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.stempo")
@RequiredArgsConstructor
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler({
            InvalidFileNameException.class,
            InvalidFileAttributeException.class,
            InvalidColumnException.class,
            SortingArgumentException.class,
            MalformedJsonException.class
    })
    public ErrorResponse<Exception> handleBadRequest(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return ErrorResponse.failure(e);
    }

    @ExceptionHandler({
            PermissionDeniedException.class
    })
    public ApiResponse<Void> handleForbidden(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        return ApiResponse.failure();
    }

    @ExceptionHandler({
            NotFoundException.class,
    })
    public ErrorResponse<Exception> handleNotFound(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        return ErrorResponse.failure(e);
    }

    @ExceptionHandler({
            EncryptionException.class,
            DecryptionException.class,
            DirectoryCreationException.class,
            FilePermissionException.class,
            Exception.class
    })
    public ErrorResponse<Exception> handleServerError(HttpServletResponse response, Exception e) {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return ErrorResponse.failure(e);
    }
}
