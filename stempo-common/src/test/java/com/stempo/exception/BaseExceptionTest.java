package com.stempo.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class BaseExceptionTest {

    @Test
    void 기본_메시지와_상태코드로_BaseException을_생성한다() {
        // given
        ErrorCode errorCode = ErrorCode.INVALID_PASSWORD;

        // when
        BaseException exception = new BaseException(errorCode);

        // then
        assertEquals(errorCode.getDefaultMessage(), exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getErrorCode().getStatus());
    }

    @Test
    void 커스텀_메시지와_상태코드로_BaseException을_생성한다() {
        // given
        ErrorCode errorCode = ErrorCode.USER_ALREADY_EXISTS;
        String customMessage = "사용자가 이미 등록되어 있습니다.";

        // when
        BaseException exception = new BaseException(errorCode, customMessage);

        // then
        assertEquals(customMessage, exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(HttpStatus.CONFLICT, exception.getErrorCode().getStatus());
    }

    @Test
    void 커스텀_메시지가_null일_경우_기본_메시지와_상태코드가_사용된다() {
        // given
        ErrorCode errorCode = ErrorCode.DATA_INTEGRITY_ERROR;
        String customMessage = null;

        // when
        BaseException exception = new BaseException(errorCode, customMessage);

        // then
        assertEquals(errorCode.getDefaultMessage(), exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getErrorCode().getStatus());
    }

    @Test
    void 커스텀_메시지가_빈_문자열일_경우_기본_메시지와_상태코드가_사용된다() {
        // given
        ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;
        String customMessage = "";

        // when
        BaseException exception = new BaseException(errorCode, customMessage);

        // then
        assertEquals(errorCode.getDefaultMessage(), exception.getMessage());
        assertEquals(errorCode, exception.getErrorCode());
        assertEquals(HttpStatus.NOT_FOUND, exception.getErrorCode().getStatus());
    }
}
