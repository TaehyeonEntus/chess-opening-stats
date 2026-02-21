package com.chessopeningstats.backend.exception;

import com.chessopeningstats.backend.exception.handler.error.ErrorCode;

public class PlayerAlreadyLinkedException extends BusinessException{
    public PlayerAlreadyLinkedException() {
        super(ErrorCode.PLAYER_ALREADY_LINKED);
    }
}
