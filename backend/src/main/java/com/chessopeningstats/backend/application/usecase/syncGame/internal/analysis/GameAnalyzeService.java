package com.chessopeningstats.backend.application.usecase.syncGame.internal.analysis;

import com.chessopeningstats.backend.application.usecase.syncGame.internal.analysis.dto.AnalyzedGameDto;
import com.chessopeningstats.backend.application.usecase.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.Game;
import com.chessopeningstats.backend.domain.GameOpening;
import com.chessopeningstats.backend.domain.GamePlayer;
import com.chessopeningstats.backend.domain.Opening;
import com.chessopeningstats.backend.util.LogExecutionTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GameAnalyzeService {
    private final OpeningAnalyzeService openingAnalyzeService;

    public Flux<AnalyzedGameDto> analyzeAll(Flux<NormalizedGameDto> dtos) {
        return dtos
                .map(this::analyzeOne)
                .filter(this::hasOpening);
    }

    public AnalyzedGameDto analyzeOne(NormalizedGameDto dto) {
        List<Opening> openings = openingAnalyzeService.analyzeOpening(dto.getPgn());

        Game game = Game.builder()
                .id(dto.getUuid())
                .time(dto.getGameTime())
                .type(dto.getGameType())
                .lastMatchedOpening(openings.isEmpty() ? null : openings.getLast())
                .playedAt(dto.getPlayedAt())
                .build();

        Set<GameOpening> gameOpenings = openings.stream()
                .map(opening -> GameOpening.builder()
                        .game(game)
                        .opening(opening)
                        .build())
                .collect(Collectors.toSet());

        GamePlayer gamePlayer = GamePlayer.builder()
                .game(game)
                .color(dto.getGamePlayerColor())
                .result(dto.getGamePlayerResult())
                .build();

        return AnalyzedGameDto.builder()
                .game(game)
                .gamePlayer(gamePlayer)
                .gameOpenings(gameOpenings)
                .playedAt(dto.getPlayedAt())
                .build();
    }

    private boolean hasOpening(AnalyzedGameDto dto) {
        return !dto.getGameOpenings().isEmpty();
    }
}



