package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class AccountNotFoundException extends BusinessException {
    public AccountNotFoundException() {
        super(ErrorCode.PLAYER_NOT_FOUND);
    }

    public AccountNotFoundException(String message) {
        super(ErrorCode.PLAYER_NOT_FOUND, message);
    }
}