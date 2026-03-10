package com.chessopeningstats.backend.domain;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Platform {
    CHESS_COM,
    LICHESS;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
