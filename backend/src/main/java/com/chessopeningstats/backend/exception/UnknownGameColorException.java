package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class UnknownGameColorException extends BusinessException {
    public UnknownGameColorException() {
        super(ErrorCode.UNRECOGNIZED_GAME_DATA);
    }

    public UnknownGameColorException(String message) {
        super(ErrorCode.UNRECOGNIZED_GAME_DATA, message);
    }

    public UnknownGameColorException(Throwable cause) {
        super(ErrorCode.UNRECOGNIZED_GAME_DATA, cause);
    }
}
