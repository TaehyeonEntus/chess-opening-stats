package com.chessopeningstats.backend.exception;

public class NicknameAlreadyExistsException extends BusinessException {
    public NicknameAlreadyExistsException() {
        super("Nickname already exists");
    }

    public NicknameAlreadyExistsException(String message) {
        super(message);
    }

    public NicknameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public NicknameAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}
