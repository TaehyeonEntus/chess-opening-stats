package com.chessopeningstats.backend.web.view.dto.home;

import com.chessopeningstats.backend.domain.Platform;

import java.time.Instant;

public record PlayerSummary(
        long id,
        String username,
        Platform platform,
        Instant lastPlayedAt
) {
}
