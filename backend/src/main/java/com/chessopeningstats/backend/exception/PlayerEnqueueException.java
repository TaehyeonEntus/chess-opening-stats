package com.chessopeningstats.backend.exception;

public class PlayerEnqueueException extends BusinessException {
    public PlayerEnqueueException() {
        super("Player enqueue failed");
    }

    public PlayerEnqueueException(String message) {
        super(message);
    }

    public PlayerEnqueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerEnqueueException(Throwable cause) {
        super(cause);
    }
}
