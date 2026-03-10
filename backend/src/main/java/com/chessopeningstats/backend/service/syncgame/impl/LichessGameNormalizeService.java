package com.chessopeningstats.backend.service.syncgame.impl;


import com.chessopeningstats.backend.domain.*;
import com.chessopeningstats.backend.infra.client.playergames.dto.LichessRawGame;
import com.chessopeningstats.backend.service.syncgame.GameNormalizeService;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LichessGameNormalizeService implements GameNormalizeService<LichessRawGame> {
    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public NormalizedGame normalizeOne(LichessRawGame rawGame, String username) {
        String uuid = rawGame.getId();
        String pgn = rawGame.getPgn();
        GameTime gameTime = parseGameTime(rawGame);
        GameType gameType = parseGameType(rawGame);
        GamePlayerColor gamePlayerColor = parseGameColor(rawGame, username);
        GamePlayerResult gamePlayerResult = parseGameResult(rawGame, gamePlayerColor);
        Instant playedAt = Instant.ofEpochMilli(rawGame.getCreatedAt());

        return new NormalizedGame(
                uuid,
                pgn,
                gameTime,
                gameType,
                playedAt,
                gamePlayerColor,
                gamePlayerResult
        );
    }

    private GameType parseGameType(LichessRawGame rawGame) {
        return switch (rawGame.getVariant()) {
            case "standard" -> GameType.STANDARD;
            case "chess960",
                 "crazyhouse",
                 "antichess",
                 "atomic",
                 "horde",
                 "kingOfTheHill",
                 "racingKings",
                 "threeCheck",
                 "fromPosition" -> GameType.ETC;
            default -> GameType.UNKNOWN;
        };
    }

    private GameTime parseGameTime(LichessRawGame rawGame) {
        return switch (rawGame.getSpeed()) {
            case "ultraBullet",
                 "bullet" -> GameTime.BULLET;
            case "blitz" -> GameTime.BLITZ;
            case "rapid" -> GameTime.RAPID;
            case "classical" -> GameTime.CLASSICAL;
            case "correspondence" -> GameTime.DAILY;
            default -> GameTime.UNKNOWN;
        };
    }

    private GamePlayerColor parseGameColor(LichessRawGame rawGame, String username) {
        if (rawGame.getPlayers().getWhite().getUser().getId().equals(username))
            return GamePlayerColor.WHITE;

        if (rawGame.getPlayers().getBlack().getUser().getId().equals(username))
            return GamePlayerColor.BLACK;

        return GamePlayerColor.UNKNOWN;
    }

    private GamePlayerResult parseGameResult(LichessRawGame rawGame, GamePlayerColor color) {
        if (hasWinner(rawGame))
            return switch (rawGame.getWinner()) {
                case "white" -> color == GamePlayerColor.WHITE
                        ? GamePlayerResult.WIN
                        : GamePlayerResult.LOSE;
                case "black" -> color == GamePlayerColor.BLACK
                        ? GamePlayerResult.WIN
                        : GamePlayerResult.LOSE;
                default -> GamePlayerResult.UNKNOWN;
            };
        else
            return switch (rawGame.getStatus()) {
                case "draw",
                     "stalemate",
                     "insufficientMaterialClaim" -> GamePlayerResult.DRAW;
                case "created",
                     "started",
                     "aborted",
                     "noStart",
                     "variantEnd",
                     "cheat",
                     "unknownFinish" -> GamePlayerResult.ETC;
                default -> GamePlayerResult.UNKNOWN;
            };
    }

    private boolean hasWinner(LichessRawGame rawGame) {
        return rawGame.getWinner() != null;
    }
}
