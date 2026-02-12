package com.chessopeningstats.backend.domain;

import lombok.ToString;

@ToString
public enum GameTime {

    BULLET,
    BLITZ,
    RAPID,
    CLASSICAL,
    DAILY,
    ETC,
    UNKNOWN
}
