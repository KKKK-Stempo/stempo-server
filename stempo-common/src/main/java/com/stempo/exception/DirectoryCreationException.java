package com.stempo.exception;

public class DirectoryCreationException extends RuntimeException {

    public DirectoryCreationException(String message) {
        super(message);
    }

    public DirectoryCreationException(String message, Throwable cause) {
        super(message, cause);
    }
}
