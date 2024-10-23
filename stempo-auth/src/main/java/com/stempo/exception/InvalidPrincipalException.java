package com.stempo.exception;

public class InvalidPrincipalException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "인증 정보가 유효하지 않습니다.";

    public InvalidPrincipalException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidPrincipalException(String s) {
        super(s);
    }
}
