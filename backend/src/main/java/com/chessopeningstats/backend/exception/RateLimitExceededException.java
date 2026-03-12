package com.chessopeningstats.backend.exception;

public class RateLimitExceededException extends BusinessException {
    public RateLimitExceededException() {
        super("Rate limit exceeded");
    }

    public RateLimitExceededException(String message) {
        super(message);
    }

    public RateLimitExceededException(String message, Throwable cause) {
        super(message, cause);
    }

    public RateLimitExceededException(Throwable cause) {
        super(cause);
    }
}
