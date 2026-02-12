package com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt;

import com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.domain.GamePlayerResult;
import com.chessopeningstats.backend.domain.GameTime;
import com.chessopeningstats.backend.domain.GameType;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.infra.client.fetchGameClient.chesscom.ChessComGameDto;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;

@Service
public class ChessComGameAdaptService implements GameAdaptService<ChessComGameDto> {
    @Override
    public NormalizedGameDto adaptOne(Account account, ChessComGameDto dto) {

        String uuid = dto.getUuid();

        String pgn = dto.getPgn();

        GameTime gameTime = parseGameTime(dto);

        GameType gameType = parseGameType(dto);

        GamePlayerColor gamePlayerColor = parseGameColor(dto, account.getUsername());

        GamePlayerResult gamePlayerResult = parseGameResult(dto, gamePlayerColor);

        Instant playedAt = Instant.ofEpochSecond(dto.getEndTime());

        boolean rated = dto.isRated();

        int rating = rated ? parseRating(dto, gamePlayerColor) : 0;

        return NormalizedGameDto.builder()
                .uuid(uuid)
                .pgn(pgn)
                .gameTime(gameTime)
                .gameType(gameType)
                .gamePlayerColor(gamePlayerColor)
                .gamePlayerResult(gamePlayerResult)
                .playedAt(playedAt)
                .rated(rated)
                .rating(rating)
                .build();
    }

    private GameType parseGameType(ChessComGameDto dto) {
        return switch (dto.getRules()) {
            case "chess" -> GameType.STANDARD;
            case "chess960",
                 "bughouse",
                 "kingofthehill",
                 "threecheck",
                 "crazyhouse" -> GameType.ETC;
            default -> GameType.UNKNOWN;
        };
    }

    private GameTime parseGameTime(ChessComGameDto dto) {
        return switch (dto.getTimeClass()) {
            case "bullet" -> GameTime.BULLET;
            case "blitz" -> GameTime.BLITZ;
            case "rapid" -> GameTime.RAPID;
            case "daily" -> GameTime.DAILY;
            default -> GameTime.UNKNOWN;
        };
    }

    private GamePlayerColor parseGameColor(ChessComGameDto dto, String username) {
        if (parseUsernameFromUrl(dto.getWhite().getId()).equals(username))
            return GamePlayerColor.WHITE;
        else if (parseUsernameFromUrl(dto.getBlack().getId()).equals(username))
            return GamePlayerColor.BLACK;
        else
            return GamePlayerColor.UNKNOWN;
    }

    private String parseUsernameFromUrl(String url) {
        return Arrays.stream(url.split("/")).toList().getLast();
    }

    private GamePlayerResult parseGameResult(ChessComGameDto dto, GamePlayerColor color) {
        String result = color == GamePlayerColor.WHITE ?
                dto.getWhite().getResult() : dto.getBlack().getResult();

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

    private int parseRating(ChessComGameDto dto, GamePlayerColor color) {
        return color == GamePlayerColor.WHITE ?
                dto.getWhite().getRating() : dto.getBlack().getRating();
    }
}

