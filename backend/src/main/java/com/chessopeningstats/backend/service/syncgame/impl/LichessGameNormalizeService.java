package com.chessopeningstats.backend.service.syncgame.impl;


import com.chessopeningstats.backend.domain.*;
import com.chessopeningstats.backend.infra.client.playergames.dto.LichessRawGame;
import com.chessopeningstats.backend.service.syncgame.GameNormalizeService;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.ParallelFlux;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LichessGameNormalizeService implements GameNormalizeService<LichessRawGame> {
    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public ParallelFlux<NormalizedGame> normalize(ParallelFlux<LichessRawGame> rawGames, Player player) {
        return rawGames.map(dto -> normalizeOne(dto, player.username()));
    }

    public NormalizedGame normalizeOne(LichessRawGame rawGame, String username) {
        String pgn = rawGame.getPgn();
        Time time = parseGameTime(rawGame);
        Type type = parseGameType(rawGame);
        Color color = parseGameColor(rawGame, username);
        Result result = parseGameResult(rawGame, color);
        Instant playedAt = Instant.ofEpochMilli(rawGame.getCreatedAt());

        return new NormalizedGame(
                pgn,
                time,
                type,
                playedAt,
                color,
                result
        );
    }

    private Type parseGameType(LichessRawGame rawGame) {
        return switch (rawGame.getVariant()) {
            case "standard" -> Type.STANDARD;
            case "chess960",
                 "crazyhouse",
                 "antichess",
                 "atomic",
                 "horde",
                 "kingOfTheHill",
                 "racingKings",
                 "threeCheck",
                 "fromPosition" -> Type.ETC;
            default -> Type.UNKNOWN;
        };
    }

    private Time parseGameTime(LichessRawGame rawGame) {
        return switch (rawGame.getSpeed()) {
            case "ultraBullet",
                 "bullet" -> Time.BULLET;
            case "blitz" -> Time.BLITZ;
            case "rapid" -> Time.RAPID;
            case "classical" -> Time.CLASSICAL;
            case "correspondence" -> Time.DAILY;
            default -> Time.UNKNOWN;
        };
    }

    private Color parseGameColor(LichessRawGame rawGame, String username) {
        if (rawGame.getPlayers().getWhite().getUser().getId().equals(username))
            return Color.WHITE;

        if (rawGame.getPlayers().getBlack().getUser().getId().equals(username))
            return Color.BLACK;

        return Color.UNKNOWN;
    }

    private Result parseGameResult(LichessRawGame rawGame, Color color) {
        if (hasWinner(rawGame))
            return switch (rawGame.getWinner()) {
                case "white" -> color == Color.WHITE
                        ? Result.WIN
                        : Result.LOSE;
                case "black" -> color == Color.BLACK
                        ? Result.WIN
                        : Result.LOSE;
                default -> Result.UNKNOWN;
            };
        else
            return switch (rawGame.getStatus()) {
                case "draw",
                     "stalemate",
                     "insufficientMaterialClaim" -> Result.DRAW;
                default -> Result.UNKNOWN;
            };
    }

    private boolean hasWinner(LichessRawGame rawGame) {
        return rawGame.getWinner() != null;
    }
}
