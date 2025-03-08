package com.stempo.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.stempo.dto.ErrorResponse;
import com.stempo.logging.constants.MdcConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @AfterEach
    void tearDown() {
        MDC.clear();
    }

    @Test
    void handleBaseException_응답이_BaseException_에_따라_설정된다() {
        // given
        ErrorCode errorCode = ErrorCode.ILLEGAL_ARGUMENT;
        BaseException baseException = new BaseException(errorCode, errorCode.getDefaultMessage());

        // when
        ErrorResponse<Exception> response = globalExceptionHandler.handleBaseException(mockResponse, baseException);

        // then
        verify(mockResponse).setStatus(errorCode.getStatus().value());
        assertEquals(false, response.getSuccess());
        assertEquals(errorCode.name(), response.getErrorMessage());
        // MDC 검증
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_CLASS.getKey()))
            .isEqualTo(baseException.getClass().getName());
        assertThat(MDC.get(MdcConstants.MDC_ERROR_CODE.getKey()))
            .isEqualTo(errorCode.name());
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_MESSAGE.getKey()))
            .isEqualTo(baseException.getMessage());
        if (baseException.getStackTrace().length > 0) {
            assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_AT.getKey()))
                .isEqualTo(baseException.getStackTrace()[0].toString());
        }
        assertThat(MDC.get(MdcConstants.MDC_HTTP_STATUS.getKey()))
            .isEqualTo(String.valueOf(errorCode.getStatus().value()));
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
        // MDC 검증
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_CLASS.getKey()))
            .isEqualTo(generalException.getClass().getName());
        assertThat(MDC.get(MdcConstants.MDC_ERROR_CODE.getKey()))
            .isEqualTo(expectedErrorCode.name());
        assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_MESSAGE.getKey()))
            .isEqualTo(expectedErrorCode.getDefaultMessage());
        if (generalException.getStackTrace().length > 0) {
            assertThat(MDC.get(MdcConstants.MDC_EXCEPTION_AT.getKey()))
                .isEqualTo(generalException.getStackTrace()[0].toString());
        }
        assertThat(MDC.get(MdcConstants.MDC_HTTP_STATUS.getKey()))
            .isEqualTo(String.valueOf(expectedErrorCode.getStatus().value()));
    }
}
