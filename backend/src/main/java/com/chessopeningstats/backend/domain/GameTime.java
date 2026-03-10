package com.chessopeningstats.backend.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;

@ToString
public enum GameTime {
    BULLET,
    BLITZ,
    RAPID,
    CLASSICAL,
    DAILY,
    UNKNOWN;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
