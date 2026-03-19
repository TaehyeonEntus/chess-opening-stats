package com.chessopeningstats.backend.domain;

public record Opening(
        Long id,
        String eco,
        String name,
        String epd,
        String pgn,
        long key
) {
}
