package com.chessopeningstats.backend.service.syncgame.impl;

import com.chessopeningstats.backend.domain.Color;
import com.chessopeningstats.backend.domain.Result;
import com.chessopeningstats.backend.domain.Time;
import com.chessopeningstats.backend.domain.Type;
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
        return validType(normalizedGame.type()) &&
                validTime(normalizedGame.time()) &&
                validColor(normalizedGame.color()) &&
                validResult(normalizedGame.result()) &&
                validPgn(normalizedGame.pgn());
    }

    private boolean validType(Type type) {
        return type == Type.STANDARD;
    }

    private boolean validTime(Time time) {
        return time != Time.UNKNOWN;
    }

    private boolean validColor(Color color) {
        return color != Color.UNKNOWN;
    }

    private boolean validResult(Result result) {
        return result != Result.UNKNOWN;
    }

    private boolean validPgn(String pgn) {
        return !pgn.isBlank();
    }
}
