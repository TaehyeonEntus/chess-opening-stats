package com.chessopeningstats.backend.domain;

public record Player(
        Platform platform,
        String username
) {
    public static Player of(Platform platform, String username) {
        return new Player(platform, username);
    }
}
