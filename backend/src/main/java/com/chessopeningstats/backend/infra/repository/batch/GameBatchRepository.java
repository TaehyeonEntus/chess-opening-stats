package com.chessopeningstats.backend.infra.repository.batch;

import com.chessopeningstats.backend.infra.repository.batch.dto.GameRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class GameBatchRepository {
    private final JdbcTemplate jdbcTemplate;

    public void deleteBatch(List<Long> gameIds) {
        jdbcTemplate.batchUpdate(
                "delete from game where id = ?",
                gameIds,
                1000,
                (ps, gameId) -> ps.setLong(1, gameId)
        );
    }

    public void upsertBatch(List<GameRow> gameRows) {
        jdbcTemplate.batchUpdate("""
                        insert ignore into game 
                            (id, time, type, played_at, last_matched_opening_id, created_at, updated_at, opening_ids)
                        values 
                            (?, ?, ?, ?, ?, now(), now(), ?)
                        """,
                gameRows,
                1000,
                (ps, gameRow) -> {
                    ps.setString(1, gameRow.uuid());
                    ps.setString(2, gameRow.gameTime().name());
                    ps.setString(3, gameRow.gameType().name());
                    ps.setTimestamp(4, Timestamp.from(gameRow.playedAt()));
                    if (gameRow.lastMatchedOpeningId() == null)
                        ps.setNull(5, java.sql.Types.BIGINT);
                    else
                        ps.setLong(5, gameRow.lastMatchedOpeningId());
                    ps.setString(6, gameRow.openingIds());
                }
        );
    }
}
