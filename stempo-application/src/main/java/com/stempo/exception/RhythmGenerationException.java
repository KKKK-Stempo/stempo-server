package com.stempo.exception;

public class RhythmGenerationException extends RuntimeException {

    public RhythmGenerationException(String message) {
        super(message);
    }

    public RhythmGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}
