package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class UsernameAlreadyExistsException extends BusinessException {
    public UsernameAlreadyExistsException() {
        super(ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    public UsernameAlreadyExistsException(String message) {
        super(ErrorCode.USERNAME_ALREADY_EXISTS, message);
    }
}
