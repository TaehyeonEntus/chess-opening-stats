package com.chessopeningstats.backend.service.syncgame.dto;

import com.chessopeningstats.backend.infra.repository.batch.dto.GamePlayerRow;
import com.chessopeningstats.backend.infra.repository.batch.dto.GameRow;

import java.util.List;

public record GameSyncBatch(
        List<GameRow> gameRows,
        List<GamePlayerRow> gamePlayerRows
) {
}
