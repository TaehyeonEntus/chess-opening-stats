package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class PasswordMismatchException extends BusinessException {
    public PasswordMismatchException(){ super(ErrorCode.PASSWORD_MISMATCH);}
    public PasswordMismatchException(String message) {
        super(ErrorCode.PASSWORD_MISMATCH, message);
    }
}
