package com.chessopeningstats.backend.exception;

public class UsernameAlreadyExistsException extends BusinessException {
    public UsernameAlreadyExistsException() {
        super("Username already exists");
    }

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }

    public UsernameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
