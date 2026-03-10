package com.chessopeningstats.backend.service.syncgame.dto;

import com.chessopeningstats.backend.infra.repository.batch.dto.GamePlayerRow;
import com.chessopeningstats.backend.infra.repository.batch.dto.GameRow;


public record AnalyzedGame(
        GameRow gameRow,
        GamePlayerRow gamePlayerRow
) {
}
