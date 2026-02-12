package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class InvalidPgnFormatException extends BusinessException {
    public InvalidPgnFormatException(String message) {
        super(ErrorCode.INVALID_PGN_FORMAT, message);
    }

    public InvalidPgnFormatException(Throwable cause) {
        super(ErrorCode.INVALID_PGN_FORMAT, cause);
    }
}
