package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class RemoteApiServerException extends BusinessException {
    public RemoteApiServerException() {
        super(ErrorCode.REMOTE_API_ERROR);
    }

    public RemoteApiServerException(String message) {
        super(ErrorCode.REMOTE_API_ERROR, message);

    }
}
