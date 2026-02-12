package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class UnsupportedPlatformException extends BusinessException {
    public UnsupportedPlatformException(String platform) {
        super(ErrorCode.UNSUPPORTED_PLATFORM, "지원하지 않는 플랫폼입니다: " + platform);
    }
}
