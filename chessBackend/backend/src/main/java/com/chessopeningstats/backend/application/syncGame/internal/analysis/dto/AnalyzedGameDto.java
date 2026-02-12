package com.chessopeningstats.backend.application.syncGame.internal.analysis.dto;

import com.chessopeningstats.backend.domain.Game;
import com.chessopeningstats.backend.domain.GameOpening;
import com.chessopeningstats.backend.domain.GamePlayer;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Collection;

@Data
@Builder
public class AnalyzedGameDto {
    private final Game game;
    private final GamePlayer gamePlayer;
    private final Collection<GameOpening> gameOpenings;
    private final Instant playedAt;
}
