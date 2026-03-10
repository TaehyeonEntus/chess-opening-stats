package com.chessopeningstats.backend.infra.repository.batch;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PlayerBatchRepository {
    private final JdbcTemplate jdbcTemplate;

    public void deleteBatch(List<Long> playerIds) {
        jdbcTemplate.batchUpdate(
                "delete from player where id = ?",
                playerIds,
                1000, // batch size
                (ps, playerId) -> ps.setLong(1, playerId)
        );
    }
}
