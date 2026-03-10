package com.chessopeningstats.backend.web.account.dto;

import com.chessopeningstats.backend.domain.Platform;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LinkPlayerRequest(
        @NotBlank String username,
        @NotNull Platform platform
) {
}
