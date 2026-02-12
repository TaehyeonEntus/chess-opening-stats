package com.chessopeningstats.backend.application.syncGame.internal.provide.internal.adapt.dto;

import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.domain.GamePlayerResult;
import com.chessopeningstats.backend.domain.GameTime;
import com.chessopeningstats.backend.domain.GameType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * 외부 플랫폼(Chess.com, Lichess)에서 가져온 게임 데이터를
 * 우리 시스템에서 분석하기 좋게 정규화한 DTO
 */
@Data
@Builder
public class NormalizedGameDto {
    // 1. 게임 정보
    private final String uuid;           // 외부 게임 고유 ID

    private final String pgn;

    private final GameTime gameTime;         // BLITZ, RAPID, BULLET
    private final GameType gameType;        // STANDARD

    private final boolean rated;        // 레이팅 게임 여부
    private final int rating;          // 경기 당시 나의 레이팅

    private final Instant playedAt;      // 경기 종료 시각

    // 2. 내 정보
    private final GamePlayerColor gamePlayerColor;     // 내가 백이었는지 흑이었는지
    private final GamePlayerResult gamePlayerResult;   // 나의 승패 결과 (WIN, LOSS, DRAW)

}
