package com.chessopeningstats.backend.domain;

import java.util.Objects;

public record Player(
        Platform platform,
        String username
) {
    public static Player of(Platform platform, String username) {
        return new Player(platform, username);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(username, player.username) && platform == player.platform;
    }

    @Override
    public int hashCode() {
        return Objects.hash(platform, username);
    }
}
