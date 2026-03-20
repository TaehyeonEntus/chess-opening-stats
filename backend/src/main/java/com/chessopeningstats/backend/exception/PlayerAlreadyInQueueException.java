package com.chessopeningstats.backend.exception;

public class PlayerAlreadyInQueueException extends BusinessException {
    public PlayerAlreadyInQueueException() {
        super("Player already in queue");
    }

    public PlayerAlreadyInQueueException(String message) {
        super(message);
    }

    public PlayerAlreadyInQueueException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerAlreadyInQueueException(Throwable cause) {
        super(cause);
    }
}
