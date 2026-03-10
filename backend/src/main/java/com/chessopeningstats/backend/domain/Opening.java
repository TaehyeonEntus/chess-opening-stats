package com.chessopeningstats.backend.domain;

public record Opening(
        Long id,
        String eco,
        String name,
        String pgn,
        String epd
) {
}
