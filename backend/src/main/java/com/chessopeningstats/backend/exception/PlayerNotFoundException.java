package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class PlayerNotFoundException extends BusinessException {
    public PlayerNotFoundException() {
        super(ErrorCode.PLAYER_NOT_FOUND);
    }

    public PlayerNotFoundException(String message) {
        super(ErrorCode.PLAYER_NOT_FOUND, message);
    }
}
