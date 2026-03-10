package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.domain.Platform;

public class UsernameNotFoundOnPlatformException extends BusinessException {
    public UsernameNotFoundOnPlatformException() {
    }

    public UsernameNotFoundOnPlatformException(String username, Platform platform) {
        super(String.format("Username[%s] not found on platform [%s]", username, platform));
    }

    public UsernameNotFoundOnPlatformException(String message) {
        super(message);
    }

    public UsernameNotFoundOnPlatformException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameNotFoundOnPlatformException(Throwable cause) {
        super(cause);
    }
}
