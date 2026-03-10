package com.chessopeningstats.backend.infra.repository.batch;

import com.chessopeningstats.backend.infra.repository.batch.dto.GamePlayerRow;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GamePlayerBatchRepository {
    private final JdbcTemplate jdbcTemplate;

    public void upsertBatch(List<GamePlayerRow> gamePlayerRows) {
        jdbcTemplate.batchUpdate("""
                            insert ignore into game_player 
                                (game_id, player_id, color, result)
                            values 
                                (?, ?, ?, ?)
                        """,
                gamePlayerRows,
                1000,
                (ps, gamePlayerRow) -> {
                    ps.setString(1, gamePlayerRow.gameId());
                    ps.setLong(2, gamePlayerRow.playerId());
                    ps.setString(3, gamePlayerRow.color().name());
                    ps.setString(4, gamePlayerRow.result().name());
                }
        );
    }
}
