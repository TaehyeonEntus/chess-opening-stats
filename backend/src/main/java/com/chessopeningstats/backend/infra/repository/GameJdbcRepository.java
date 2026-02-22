package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Collection;

@Repository
@RequiredArgsConstructor
public class GameJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsertGames(Collection<Game> games) {
        jdbcTemplate.batchUpdate("""
                    insert ignore into game (id, played_at, time, type, last_matched_opening_id, created_at, updated_at)
                    values (?, ?, ?, ?, ?, now(), now())
                """, games, 1000, (ps, game) -> {
            ps.setString(1, game.getId());
            ps.setTimestamp(2, Timestamp.from(game.getPlayedAt()));
            ps.setString(3, game.getTime().name());
            ps.setString(4, game.getType().name());
            ps.setLong(5, game.getLastMatchedOpening().getId());
        });
    }
}

