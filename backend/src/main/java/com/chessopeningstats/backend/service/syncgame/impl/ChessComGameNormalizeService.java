package com.chessopeningstats.backend.service.syncgame.impl;


import com.chessopeningstats.backend.domain.*;
import com.chessopeningstats.backend.infra.client.playergames.dto.ChessComRawGame;
import com.chessopeningstats.backend.service.syncgame.GameNormalizeService;
import com.chessopeningstats.backend.service.syncgame.dto.NormalizedGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ChessComGameNormalizeService implements GameNormalizeService<ChessComRawGame> {
    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public NormalizedGame normalizeOne(ChessComRawGame dto, String username) {
        String uuid = dto.getUuid();
        String pgn = dto.getPgn();
        GameTime gameTime = parseGameTime(dto);
        GameType gameType = parseGameType(dto);
        GamePlayerColor gamePlayerColor = parseGameColor(dto, username);
        GamePlayerResult gamePlayerResult = parseGameResult(dto, gamePlayerColor);
        Instant playedAt = Instant.ofEpochSecond(dto.getEndTime());

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

    private GameType parseGameType(ChessComRawGame rawGame) {
        return switch (rawGame.getRules()) {
            case "chess" -> GameType.STANDARD;
            case "chess960",
                 "bughouse",
                 "kingofthehill",
                 "threecheck",
                 "crazyhouse" -> GameType.ETC;
            default -> GameType.UNKNOWN;
        };
    }

    private GameTime parseGameTime(ChessComRawGame rawGame) {
        return switch (rawGame.getTimeClass()) {
            case "bullet" -> GameTime.BULLET;
            case "blitz" -> GameTime.BLITZ;
            case "rapid" -> GameTime.RAPID;
            case "daily" -> GameTime.DAILY;
            default -> GameTime.UNKNOWN;
        };
    }

    private GamePlayerColor parseGameColor(ChessComRawGame rawGame, String username) {
        if (parseUsernameFromUrl(rawGame.getWhite().getId()).equals(username))
            return GamePlayerColor.WHITE;

        if (parseUsernameFromUrl(rawGame.getBlack().getId()).equals(username))
            return GamePlayerColor.BLACK;

        return GamePlayerColor.UNKNOWN;
    }

    private String parseUsernameFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    private GamePlayerResult parseGameResult(ChessComRawGame rawGame, GamePlayerColor color) {
        String result = (color == GamePlayerColor.WHITE)
                ? rawGame.getWhite().getResult()
                : rawGame.getBlack().getResult();

        return switch (result) {
            case "win" -> GamePlayerResult.WIN;
            case "agreed",
                 "repetition",
                 "stalemate",
                 "insufficient",
                 "timevsinsufficient",
                 "50move" -> GamePlayerResult.DRAW;
            case "lose",
                 "checkmated",
                 "resigned",
                 "timeout",
                 "abandoned" -> GamePlayerResult.LOSE;
            case "kingofthehill",
                 "threecheck",
                 "bughousepartnerlose" -> GamePlayerResult.ETC;
            default -> GamePlayerResult.UNKNOWN;
        };
    }
}
