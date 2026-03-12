package com.chessopeningstats.backend.exception;

public class ExternalServiceException extends BusinessException {
    public ExternalServiceException() {
        super("External service error");
    }

    public ExternalServiceException(String message) {
        super(message);
    }

    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalServiceException(Throwable cause) {
        super(cause);
    }
}
