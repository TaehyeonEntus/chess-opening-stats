package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class NicknameAlreadyExistsException extends BusinessException {
    public NicknameAlreadyExistsException() {
        super(ErrorCode.NICKNAME_ALREADY_EXISTS);
    }

    public NicknameAlreadyExistsException(String message) {
        super(ErrorCode.NICKNAME_ALREADY_EXISTS, message);
    }
}
