package com.chessopeningstats.backend.infra.client.playerprofile.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ChessComPlayerProfile(
        String avatar,
        long last_online
) {
    public PlayerProfile toPlayerProfile() {
        return new PlayerProfile(
                avatar,
                Duration.of(Instant.now().getEpochSecond() - last_online, TimeUnit.SECONDS.toChronoUnit()).toSeconds()
        );
    }
}
