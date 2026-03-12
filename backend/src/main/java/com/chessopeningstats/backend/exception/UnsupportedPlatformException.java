package com.chessopeningstats.backend.exception;

public class UnsupportedPlatformException extends BusinessException {
    public UnsupportedPlatformException() {
        super("Unsupported platform");
    }

    public UnsupportedPlatformException(String message) {
        super(message);
    }

    public UnsupportedPlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedPlatformException(Throwable cause) {
        super(cause);
    }
}
