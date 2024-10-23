package com.stempo.exception;

public class AuthenticationNotFoundException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "인증 정보가 존재하지 않습니다.";

    public AuthenticationNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public AuthenticationNotFoundException(String s) {
        super(s);
    }
}
