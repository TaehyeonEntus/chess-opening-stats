package com.chessopeningstats.backend.web.account.dto;

import jakarta.validation.constraints.NotNull;

public record UnlinkPlayerRequest(
        @NotNull Long playerId
) {
}
