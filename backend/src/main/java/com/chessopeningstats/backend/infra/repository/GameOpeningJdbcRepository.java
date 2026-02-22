package com.chessopeningstats.backend.infra.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class GameOpeningJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsertGameOpenings(Collection<GameOpeningRow> gameOpeningRows) {
        jdbcTemplate.batchUpdate("""
                    insert ignore into game_opening (game_id, opening_id, created_at, updated_at)
                    values (?, ?, now(), now())
                """, gameOpeningRows, 2000, (ps, gameOpeningRow) -> {
            ps.setString(1, gameOpeningRow.gameId());
            ps.setLong(2, gameOpeningRow.openingId());
        });
    }

    public record GameOpeningRow(
            String gameId,
            long openingId
    ) {}
}

