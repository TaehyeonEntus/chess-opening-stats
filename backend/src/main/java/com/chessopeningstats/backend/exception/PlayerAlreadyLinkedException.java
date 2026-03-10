package com.chessopeningstats.backend.exception;

public class PlayerAlreadyLinkedException extends BusinessException{
    public PlayerAlreadyLinkedException() {
    }

    public PlayerAlreadyLinkedException(String message) {
        super(message);
    }

    public PlayerAlreadyLinkedException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerAlreadyLinkedException(Throwable cause) {
        super(cause);
    }
}
