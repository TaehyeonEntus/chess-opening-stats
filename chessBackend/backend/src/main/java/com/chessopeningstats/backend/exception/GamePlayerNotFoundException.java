package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class GamePlayerNotFoundException extends BusinessException {
    public GamePlayerNotFoundException() {
        super(ErrorCode.GAME_PLAYER_NOT_FOUND);
    }

    public GamePlayerNotFoundException(String message) {
        super(ErrorCode.GAME_PLAYER_NOT_FOUND, message);
    }
}
