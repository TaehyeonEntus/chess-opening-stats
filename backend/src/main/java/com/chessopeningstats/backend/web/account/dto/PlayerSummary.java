package com.chessopeningstats.backend.web.account.dto;

import com.chessopeningstats.backend.domain.Platform;

import java.time.Instant;

public record PlayerSummary(
        Long id,
        String username,
        Platform platform,
        Instant lastPlayedAt
) {
}
