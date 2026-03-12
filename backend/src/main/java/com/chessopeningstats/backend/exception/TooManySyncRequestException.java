package com.chessopeningstats.backend.exception;

public class TooManySyncRequestException extends BusinessException {
    public TooManySyncRequestException() {
        super("Too many sync requests");
    }

    public TooManySyncRequestException(String message) {
        super(message);
    }

    public TooManySyncRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public TooManySyncRequestException(Throwable cause) {
        super(cause);
    }
}
