package com.chessopeningstats.backend.infra.client.playerprofile.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LichessPlayerProfile(
        Long seenAt
) {
    public PlayerProfile toPlayerProfile() {
        return new PlayerProfile(
                null,
                Duration.of(Instant.now().toEpochMilli() - seenAt, TimeUnit.MILLISECONDS.toChronoUnit()).toSeconds()
        );
    }
}
