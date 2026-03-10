package com.chessopeningstats.backend.service.syncgame.dto;

import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.domain.GamePlayerResult;
import com.chessopeningstats.backend.domain.GameTime;
import com.chessopeningstats.backend.domain.GameType;

import java.time.Instant;

/**
 * 외부 플랫폼(Chess.com, Lichess)에서 가져온 게임 데이터를
 * 우리 시스템에서 분석하기 좋게 정규화한 DTO
 */
public record NormalizedGame(
        String uuid,
        String pgn,
        GameTime gameTime,
        GameType gameType,
        Instant playedAt,
        GamePlayerColor gamePlayerColor,
        GamePlayerResult gamePlayerResult
) {
}
