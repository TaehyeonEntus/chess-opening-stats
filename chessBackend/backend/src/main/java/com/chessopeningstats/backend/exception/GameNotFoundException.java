package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class GameNotFoundException extends BusinessException {
    public GameNotFoundException() {
        super(ErrorCode.GAME_NOT_FOUND);
    }

    public GameNotFoundException(String message) {
        super(ErrorCode.GAME_NOT_FOUND, message);
    }
}
