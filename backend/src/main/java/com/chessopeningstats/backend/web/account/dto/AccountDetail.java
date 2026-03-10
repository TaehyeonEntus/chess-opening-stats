package com.chessopeningstats.backend.web.account.dto;

import java.time.Instant;

public record AccountDetail(
        Long id,
        String username,
        String nickname,
        Instant lastSyncedAt
) {
}
