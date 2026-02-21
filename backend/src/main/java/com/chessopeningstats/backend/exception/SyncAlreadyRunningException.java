package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class SyncAlreadyRunningException extends BusinessException {
    public SyncAlreadyRunningException() {
        super(ErrorCode.SYNC_ALREADY_RUNNING);
    }
}
