package com.chessopeningstats.backend.exception;

public class PlayerAlreadyUnlinkedException extends BusinessException{
    public PlayerAlreadyUnlinkedException() {
    }

    public PlayerAlreadyUnlinkedException(String message) {
        super(message);
    }

    public PlayerAlreadyUnlinkedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerAlreadyUnlinkedException(Throwable cause) {
        super(cause);
    }
}
