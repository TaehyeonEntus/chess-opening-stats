package com.chessopeningstats.backend.exception;

public class NoLinkedPlayersException extends BusinessException {
    public NoLinkedPlayersException() {
        super("No linked players found");
    }

    public NoLinkedPlayersException(String message) {
        super(message);
    }

    public NoLinkedPlayersException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoLinkedPlayersException(Throwable cause) {
        super(cause);
    }
}
