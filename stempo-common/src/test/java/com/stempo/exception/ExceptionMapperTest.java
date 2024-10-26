package com.stempo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.stream.MalformedJsonException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.validation.ConstraintViolationException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletionException;
import org.hibernate.query.sqm.UnknownPathException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

class ExceptionMapperTest {

    @Test
    void 매핑된_예외가_올바른_ErrorCode로_반환된다() {
        // 400 BAD_REQUEST Errors
        assertEquals(ErrorCode.CONSTRAINT_VIOLATION,
                ExceptionMapper.getErrorCode(new ConstraintViolationException(null)));
        assertEquals(ErrorCode.HTTP_MESSAGE_NOT_READABLE,
                ExceptionMapper.getErrorCode(new HttpMessageNotReadableException("")));
        assertEquals(ErrorCode.ILLEGAL_ACCESS, ExceptionMapper.getErrorCode(new IllegalAccessException()));
        assertEquals(ErrorCode.ILLEGAL_ARGUMENT, ExceptionMapper.getErrorCode(new IllegalArgumentException()));
        assertEquals(ErrorCode.INVALID_DATA_ACCESS,
                ExceptionMapper.getErrorCode(new InvalidDataAccessApiUsageException("")));
        assertEquals(ErrorCode.MALFORMED_JSON,
                ExceptionMapper.getErrorCode(new MalformedJsonException("")));

        BindingResult mockBindingResult = Mockito.mock(BindingResult.class);
        assertEquals(ErrorCode.METHOD_ARGUMENT_NOT_VALID,
                ExceptionMapper.getErrorCode(new MethodArgumentNotValidException(null, mockBindingResult)));

        MethodParameter mockParameter = Mockito.mock(MethodParameter.class);
        assertEquals(ErrorCode.TYPE_MISMATCH,
                ExceptionMapper.getErrorCode(
                        new MethodArgumentTypeMismatchException(null, String.class, "param", mockParameter, null)));

        assertEquals(ErrorCode.MISSING_PARAMETER,
                ExceptionMapper.getErrorCode(new MissingServletRequestParameterException("", "")));
        assertEquals(ErrorCode.NO_SUCH_FIELD, ExceptionMapper.getErrorCode(new NoSuchFieldException()));
        assertEquals(ErrorCode.NUMBER_FORMAT_ERROR, ExceptionMapper.getErrorCode(new NumberFormatException()));
        assertEquals(ErrorCode.INDEX_OUT_OF_BOUNDS,
                ExceptionMapper.getErrorCode(new StringIndexOutOfBoundsException()));
        assertEquals(ErrorCode.UNKNOWN_PATH, ExceptionMapper.getErrorCode(new UnknownPathException("")));
        assertEquals(ErrorCode.JWT_SECURITY_ERROR,
                ExceptionMapper.getErrorCode(new io.jsonwebtoken.security.SecurityException("")));

        // 401 UNAUTHORIZED Errors
        assertEquals(ErrorCode.ACCESS_DENIED, ExceptionMapper.getErrorCode(new AccessDeniedException("")));

        AuthorizationDecision decision = new AuthorizationDecision(false);
        assertEquals(ErrorCode.ACCESS_DENIED,
                ExceptionMapper.getErrorCode(new AuthorizationDeniedException("권한 거부", decision)));

        assertEquals(ErrorCode.ACCESS_DENIED, ExceptionMapper.getErrorCode(new AuthorizationServiceException("")));
        assertEquals(ErrorCode.BAD_CREDENTIALS, ExceptionMapper.getErrorCode(new BadCredentialsException("")));
        assertEquals(ErrorCode.EXPIRED_JWT, ExceptionMapper.getErrorCode(new ExpiredJwtException(null, null, "")));
        assertEquals(ErrorCode.MALFORMED_JWT, ExceptionMapper.getErrorCode(new MalformedJwtException("")));
        assertEquals(ErrorCode.UNSUPPORTED_JWT, ExceptionMapper.getErrorCode(new UnsupportedJwtException("")));
        assertEquals(ErrorCode.USERNAME_NOT_FOUND, ExceptionMapper.getErrorCode(new UsernameNotFoundException("")));

        // 404 NOT_FOUND Errors
        assertEquals(ErrorCode.FILE_NOT_FOUND, ExceptionMapper.getErrorCode(new FileNotFoundException()));
        assertEquals(ErrorCode.ELEMENT_NOT_FOUND, ExceptionMapper.getErrorCode(new NoSuchElementException()));
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ExceptionMapper.getErrorCode(new NotFoundException()));
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, ExceptionMapper.getErrorCode(new NullPointerException()));

        // 409 CONFLICT Errors
        assertEquals(ErrorCode.ILLEGAL_STATE, ExceptionMapper.getErrorCode(new IllegalStateException()));

        // 500 INTERNAL_SERVER_ERROR Errors
        assertEquals(ErrorCode.ARRAY_INDEX_OUT_OF_BOUNDS,
                ExceptionMapper.getErrorCode(new ArrayIndexOutOfBoundsException()));
        assertEquals(ErrorCode.COMPLETION_ERROR, ExceptionMapper.getErrorCode(new CompletionException("Error", null)));
        assertEquals(ErrorCode.DATA_INTEGRITY_ERROR,
                ExceptionMapper.getErrorCode(new DataIntegrityViolationException("")));
        assertEquals(ErrorCode.IO_ERROR, ExceptionMapper.getErrorCode(new IOException()));
        assertEquals(ErrorCode.INCORRECT_RESULT_SIZE,
                ExceptionMapper.getErrorCode(new IncorrectResultSizeDataAccessException(1)));
        assertEquals(ErrorCode.SQL_ERROR, ExceptionMapper.getErrorCode(new SQLException()));
        assertEquals(ErrorCode.SECURITY_ERROR, ExceptionMapper.getErrorCode(new SecurityException("")));
        assertEquals(ErrorCode.TRANSACTION_SYSTEM_ERROR,
                ExceptionMapper.getErrorCode(new TransactionSystemException("")));
    }

    @Test
    void 매핑되지_않은_예외는_INTERNAL_SERVER_ERROR로_반환된다() {
        assertEquals(ErrorCode.INTERNAL_SERVER_ERROR, ExceptionMapper.getErrorCode(new Exception("매핑되지 않은 예외")));
    }
}
