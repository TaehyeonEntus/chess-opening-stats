package com.chessopeningstats.backend.application.usecase.provideAccountInfo.dto;

import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data(staticConstructor = "of")
public class AccountInfoResponse {
    private final String nickname;
    private final Instant lastSyncedAt;
    private final List<PlayerInfo> players;
}
