package com.chessopeningstats.backend.web.view.dto.home;

import java.time.Instant;

public record AccountSummary(
        long id,
        String nickname,
        Instant lastSyncedAt
) {
}
