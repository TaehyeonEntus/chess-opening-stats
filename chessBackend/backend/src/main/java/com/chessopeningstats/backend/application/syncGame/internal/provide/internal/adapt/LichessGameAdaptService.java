package com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt;

import com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt.dto.NormalizedGameDto;
import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.domain.GamePlayerResult;
import com.chessopeningstats.backend.domain.GameTime;
import com.chessopeningstats.backend.domain.GameType;
import com.chessopeningstats.backend.domain.Account;

import java.time.Instant;

import com.chessopeningstats.backend.infra.client.fetchGameClient.lichess.LichessGameDto;
import org.springframework.stereotype.Service;

@Service
public class LichessGameAdaptService implements GameAdaptService<LichessGameDto> {
    @Override
    public NormalizedGameDto adaptOne(Account account, LichessGameDto dto) {

        String uuid = dto.getId();

        String pgn = dto.getPgn();

        GameTime gameTime = parseGameTime(dto);

        GameType gameType = parseGameType(dto);

        GamePlayerColor gamePlayerColor = parseGameColor(dto, account.getUsername());

        GamePlayerResult gamePlayerResult = parseGameResult(dto, gamePlayerColor);

        Instant playedAt = Instant.ofEpochMilli(dto.getCreatedAt());

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

    private GameType parseGameType(LichessGameDto dto) {
        return switch (dto.getVariant()) {
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

    private GameTime parseGameTime(LichessGameDto dto) {
        return switch (dto.getSpeed()) {
            case "ultraBullet",
                 "bullet" -> GameTime.BULLET;
            case "blitz" -> GameTime.BLITZ;
            case "rapid" -> GameTime.RAPID;
            case "classical" -> GameTime.CLASSICAL;
            case "correspondence" -> GameTime.DAILY;
            default -> GameTime.UNKNOWN;
        };
    }

    private GamePlayerColor parseGameColor(LichessGameDto dto, String username) {
        if (dto.getPlayers().getWhite().getUser().getId().equals(username))
            return GamePlayerColor.WHITE;
        else if (dto.getPlayers().getBlack().getUser().getId().equals(username))
            return GamePlayerColor.BLACK;
        else
            return GamePlayerColor.UNKNOWN;
    }

    private GamePlayerResult parseGameResult(LichessGameDto dto, GamePlayerColor color) {
        if (dto.getWinner() == null) { //winner X
            return switch (dto.getStatus()) {
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
        } else { //winner O
            if (dto.getWinner().equals("white") && color == GamePlayerColor.WHITE)
                return GamePlayerResult.WIN;
            else if (dto.getWinner().equals("black") && color == GamePlayerColor.BLACK)
                return GamePlayerResult.LOSE;
            else
                return GamePlayerResult.UNKNOWN;
        }
    }

    private int parseRating(LichessGameDto dto, GamePlayerColor color) {
        return color == GamePlayerColor.WHITE ?
                dto.getPlayers().getWhite().getRating() : dto.getPlayers().getBlack().getRating();
    }
}

