package com.chessopeningstats.backend.exception;

public class PlayerDequeueException extends BusinessException {
    public PlayerDequeueException() {
        super("Player dequeue failed");
    }

    public PlayerDequeueException(String message) {
        super(message);
    }

    public PlayerDequeueException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerDequeueException(Throwable cause) {
        super(cause);
    }
}
