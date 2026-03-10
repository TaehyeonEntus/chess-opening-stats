package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.domain.GameTime;
import com.chessopeningstats.backend.domain.GameType;
import com.chessopeningstats.backend.service.syncgame.GameSanitizeService;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ParallelFlux;

@Service
@RequiredArgsConstructor
public class GameSanitizeServiceImpl implements GameSanitizeService {

    @Override
    public ParallelFlux<NormalizedGame> sanitize(ParallelFlux<NormalizedGame> normalizedGames) {
        return normalizedGames.filter(this::isValid);
    }

    private boolean isValid(NormalizedGame normalizedGame) {
        return validGameType(normalizedGame)
                && validGameTime(normalizedGame)
                && validGamePlayerColor(normalizedGame)
                && validPgn(normalizedGame);
    }

    private boolean validGameType(NormalizedGame normalizedGame) {
        return normalizedGame.gameType() == GameType.STANDARD;
    }

    private boolean validGameTime(NormalizedGame normalizedGame) {
        return normalizedGame.gameTime() != GameTime.UNKNOWN;
    }

    private boolean validGamePlayerColor(NormalizedGame normalizedGame) {
        return normalizedGame.gamePlayerColor() != GamePlayerColor.UNKNOWN;
    }

    private boolean validPgn(NormalizedGame normalizedGame) {
        return !normalizedGame.pgn().isBlank();
    }
}
