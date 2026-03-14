package com.chessopeningstats.backend.domain;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.ToString;

@ToString
public enum Type {
    STANDARD,
    ETC,
    UNKNOWN;

    @JsonValue
    public String toJson() {
        return name().toLowerCase();
    }
}
