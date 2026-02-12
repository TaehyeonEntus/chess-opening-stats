package com.chessopeningstats.backend.application.syncGame.internal.analysis;

import com.chessopeningstats.backend.application.syncGame.internal.analysis.dto.AnalyzedGameDto;
import com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.Game;
import com.chessopeningstats.backend.domain.GameOpening;
import com.chessopeningstats.backend.domain.GamePlayer;
import com.chessopeningstats.backend.domain.Opening;
import com.chessopeningstats.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameAnalyzeService {
    private final OpeningAnalyzeService openingAnalyzeService;

    @LogExecutionTime
    public Set<AnalyzedGameDto> analyzeAll(Collection<NormalizedGameDto> dtos) {
        return dtos.parallelStream()
                .map(this::analyzeOne)
                .collect(Collectors.toSet());
    }

    public AnalyzedGameDto analyzeOne(NormalizedGameDto dto) {
        Game game = Game.builder()
                .uuid(dto.getUuid())
                .time(dto.getGameTime())
                .type(dto.getGameType())
                .playedAt(dto.getPlayedAt())
                .build();

        GamePlayer gamePlayer = GamePlayer.builder()
                .color(dto.getGamePlayerColor())
                .result(dto.getGamePlayerResult())
                .build();

        Set<GameOpening> gameOpenings = openingAnalyzeService.analyzeOpening(dto.getPgn())
                .stream()
                .map(opening -> GameOpening.builder()
                        .opening(opening)
                        .build())
                .collect(Collectors.toSet());

        Instant playedAt = dto.getPlayedAt();

        return AnalyzedGameDto.builder()
                .game(game)
                .gamePlayer(gamePlayer)
                .gameOpenings(gameOpenings)
                .playedAt(playedAt)
                .build();
    }
}



