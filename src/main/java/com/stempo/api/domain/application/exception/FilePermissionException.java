package com.stempo.api.domain.application.exception;

public class FilePermissionException extends RuntimeException {

    public FilePermissionException(String message) {
        super(message);
    }
}
