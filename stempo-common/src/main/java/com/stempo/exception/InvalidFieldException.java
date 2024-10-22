package com.stempo.exception;

public class InvalidFieldException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "";

    public InvalidFieldException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidFieldException(String s) {
        super(s);
    }
}
