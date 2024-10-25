package com.stempo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.stempo.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Test
    void handleBaseException_응답이_BaseException_에_따라_설정된다() {
        // given
        ErrorCode errorCode = ErrorCode.ILLEGAL_ARGUMENT;
        BaseException baseException = new BaseException(errorCode);

        // when
        ErrorResponse<Exception> response = globalExceptionHandler.handleBaseException(mockResponse, baseException);

        // then
        verify(mockResponse).setStatus(errorCode.getStatus().value());
        assertEquals(false, response.getSuccess());
        assertEquals(errorCode.name(), response.getErrorMessage());
    }

    @Test
    void handleServerError_응답이_일반_예외에_따라_설정된다() {
        // given
        Exception generalException = new IllegalArgumentException("잘못된 매개변수");

        ErrorCode expectedErrorCode = ExceptionMapper.getErrorCode(generalException);

        // when
        ErrorResponse<Exception> response = globalExceptionHandler.handleServerError(mockRequest, mockResponse,
                generalException);

        // then
        verify(mockResponse).setStatus(expectedErrorCode.getStatus().value());
        assertEquals(false, response.getSuccess());
        assertEquals(expectedErrorCode.name(), response.getErrorMessage());
    }
}
