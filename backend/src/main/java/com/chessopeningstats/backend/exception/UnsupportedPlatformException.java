package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.domain.Platform;

public class UnsupportedPlatformException extends BusinessException {
    public UnsupportedPlatformException() {
        super("Unsupported platform");
    }

    public UnsupportedPlatformException(Platform platform) {
        super("Unsupported platform: " + platform);
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
