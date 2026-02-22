package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.domain.GamePlayerResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class GamePlayerJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsertGamePlayers(Collection<GamePlayerRow> gamePlayerRows) {
        if (gamePlayerRows == null || gamePlayerRows.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate("""
                    insert into game_player (player_id, game_id, color, result, created_at, updated_at)
                    values (?, ?, ?, ?, now(), now())
                    on duplicate key update
                        id = id
                """, gamePlayerRows, 1000, (ps, gamePlayerRow) -> {
            ps.setLong(1, gamePlayerRow.playerId());
            ps.setString(2, gamePlayerRow.gameId());
            ps.setString(3, gamePlayerRow.color().name());
            ps.setString(4, gamePlayerRow.result().name());
        });
    }

    public record GamePlayerRow(
            String gameId,
            long playerId,
            GamePlayerColor color,
            GamePlayerResult result
    ) {
    }
}
