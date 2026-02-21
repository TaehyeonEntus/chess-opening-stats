package com.chessopeningstats.backend.application.usecase.provideAccountInfo.dto;

import com.chessopeningstats.backend.domain.Platform;
import lombok.Data;

import java.time.Instant;

@Data(staticConstructor = "of")
public class PlayerInfo {
    private final String username;
    private final Platform platform;
    private final Instant lastPlayedAt;
}
