package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class UnrecognizedGameDataException extends BusinessException {
    public UnrecognizedGameDataException(String customMessage) {
        super(ErrorCode.UNRECOGNIZED_GAME_DATA, customMessage);
    }
}
