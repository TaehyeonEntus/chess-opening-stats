package com.chessopeningstats.backend.infra.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class GameOpeningJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsertGameOpenings(Collection<GameOpeningRow> gameOpeningRows) {
        if (gameOpeningRows == null || gameOpeningRows.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate("""
            insert into GAME_OPENING (GAME_ID, OPENING_ID, CREATED_AT, UPDATED_AT)
            values (?, ?, now(), now())
            on duplicate key update
                id = id
        """, gameOpeningRows, 2000, (ps, gameOpeningRow) -> {
            ps.setLong(1, gameOpeningRow.gameId());
            ps.setLong(2, gameOpeningRow.openingId());
        });
    }

    public record GameOpeningRow(
            long gameId,
            long openingId
    ) {
    }
}

