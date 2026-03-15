package com.chessopeningstats.backend.service.syncgame.impl;


import com.chessopeningstats.backend.domain.*;
import com.chessopeningstats.backend.infra.client.playergames.dto.ChessComRawGame;
import com.chessopeningstats.backend.service.syncgame.GameNormalizeService;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ParallelFlux;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChessComGameNormalizeService implements GameNormalizeService<ChessComRawGame> {
    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public ParallelFlux<NormalizedGame> normalize(ParallelFlux<ChessComRawGame> rawGames, Player player) {
        return rawGames.map(dto -> normalizeOne(dto, player.username()));
    }

    public NormalizedGame normalizeOne(ChessComRawGame dto, String username) {
        String pgn = dto.getPgn();
        Time time = parseGameTime(dto);
        Type type = parseGameType(dto);
        Color color = parseGameColor(dto, username);
        Result result = parseGameResult(dto, color);
        Instant playedAt = Instant.ofEpochSecond(dto.getEndTime());

        return new NormalizedGame(
                pgn,
                time,
                type,
                playedAt,
                color,
                result
        );
    }

    private Type parseGameType(ChessComRawGame rawGame) {
        return switch (rawGame.getRules()) {
            case "chess" -> Type.STANDARD;
            case "chess960",
                 "bughouse",
                 "kingofthehill",
                 "threecheck",
                 "crazyhouse" -> Type.ETC;
            default -> Type.UNKNOWN;
        };
    }

    private Time parseGameTime(ChessComRawGame rawGame) {
        return switch (rawGame.getTimeClass()) {
            case "bullet" -> Time.BULLET;
            case "blitz" -> Time.BLITZ;
            case "rapid" -> Time.RAPID;
            case "daily" -> Time.DAILY;
            default -> Time.UNKNOWN;
        };
    }

    private Color parseGameColor(ChessComRawGame rawGame, String username) {
        if (parseUsernameFromUrl(rawGame.getWhite().getId()).equals(username))
            return Color.WHITE;

        if (parseUsernameFromUrl(rawGame.getBlack().getId()).equals(username))
            return Color.BLACK;

        return Color.UNKNOWN;
    }

    private String parseUsernameFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    private Result parseGameResult(ChessComRawGame rawGame, Color color) {
        String result = (color == Color.WHITE)
                ? rawGame.getWhite().getResult()
                : rawGame.getBlack().getResult();

        return switch (result) {
            case "win" -> Result.WIN;
            case "agreed",
                 "repetition",
                 "stalemate",
                 "insufficient",
                 "timevsinsufficient",
                 "50move" -> Result.DRAW;
            case "lose",
                 "checkmated",
                 "resigned",
                 "timeout",
                 "abandoned" -> Result.LOSE;
            default -> Result.UNKNOWN;
        };
    }
}
