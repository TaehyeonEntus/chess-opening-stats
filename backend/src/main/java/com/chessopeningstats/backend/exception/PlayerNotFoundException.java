package com.chessopeningstats.backend.exception;

public class PlayerNotFoundException extends BusinessException {
    public PlayerNotFoundException() {
        super("Player not found");
    }

    public PlayerNotFoundException(String message) {
        super(message);
    }

    public PlayerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerNotFoundException(Throwable cause) {
        super(cause);
    }
}
