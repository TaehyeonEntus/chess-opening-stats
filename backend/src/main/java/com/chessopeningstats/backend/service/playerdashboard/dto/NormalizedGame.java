package com.chessopeningstats.backend.service.playerdashboard.dto;

import com.chessopeningstats.backend.domain.*;

import java.time.Instant;

/**
 * 외부 플랫폼(Chess.com, Lichess)에서 가져온 게임 데이터를
 * 우리 시스템에서 분석하기 좋게 정규화한 DTO
 */
public record NormalizedGame(
        String pgn,
        Time time,
        Type type,
        Instant playedAt,
        Color color,
        Result result,
        Player player
) {
}
