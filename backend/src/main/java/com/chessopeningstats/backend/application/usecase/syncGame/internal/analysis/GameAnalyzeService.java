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

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameAnalyzeService {
    private final OpeningAnalyzeService openingAnalyzeService;

    @LogExecutionTime
    public Set<AnalyzedGameDto> analyzeAll(Collection<NormalizedGameDto> dtos) {
        return dtos.parallelStream()
                .map(this::analyzeOne)
                .filter(this::hasOpening)
                .collect(Collectors.toSet());
    }

    public AnalyzedGameDto analyzeOne(NormalizedGameDto dto) {
        List<Opening> openings = openingAnalyzeService.analyzeOpening(dto.getPgn());

        Instant playedAt = dto.getPlayedAt();

        Game game = Game.builder()
                .id(dto.getUuid())
                .time(dto.getGameTime())
                .type(dto.getGameType())
                .lastMatchedOpening(openings.isEmpty()?null:openings.getLast())
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
                .playedAt(playedAt)
                .build();
    }

    private boolean hasOpening(AnalyzedGameDto dto){
        return !dto.getGameOpenings().isEmpty();
    }
}



