package com.stempo.api.global.exception;

public class PermissionDeniedException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "권한이 부족합니다.";

    public PermissionDeniedException() {
        super(DEFAULT_MESSAGE);
    }

    public PermissionDeniedException(String s) {
        super(s);
    }
}
