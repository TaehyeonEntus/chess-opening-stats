package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class RemoteApiTimeoutException extends BusinessException {
    public RemoteApiTimeoutException(String message) {
        super(ErrorCode.REMOTE_API_TIMEOUT, message);
    }

    public RemoteApiTimeoutException(Throwable cause) {
        super(ErrorCode.REMOTE_API_TIMEOUT, cause);
    }
}
