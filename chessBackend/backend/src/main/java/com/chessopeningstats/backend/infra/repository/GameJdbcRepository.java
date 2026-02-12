package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Game;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class GameJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void upsertGames(Collection<Game> games) {
        if (games == null || games.isEmpty()) {
            return;
        }

        jdbcTemplate.batchUpdate("""
            insert into GAME (UUID, PLAYED_AT, TIME, TYPE, CREATED_AT, UPDATED_AT)
            values (?, ?, ?, ?, now(), now())
            on duplicate key update
                id = id
        """, games, 1000, (ps, game) -> {
            ps.setString(1, game.getUuid());
            ps.setTimestamp(2, Timestamp.from(game.getPlayedAt()));
            ps.setString(3, game.getTime().name());
            ps.setString(4, game.getType().name());
        });
    }

    public Map<String, Long> findIdsByUuid(Set<String> uuids) {

        if (uuids == null || uuids.isEmpty()) {
            return Collections.emptyMap();
        }

        // 1️⃣ temp table 생성 (세션 단위)
        jdbcTemplate.execute("""
            CREATE TEMPORARY TABLE IF NOT EXISTS temp_uuid (
                uuid VARCHAR(255) PRIMARY KEY
            )
        """);

        // 2️⃣ 초기화
        jdbcTemplate.execute("TRUNCATE TABLE temp_uuid");

        // 3️⃣ batch insert (기존 스타일 유지)
        jdbcTemplate.batchUpdate(
                "INSERT INTO temp_uuid (uuid) VALUES (?)",
                new ArrayList<>(uuids),
                1000,
                (ps, uuid) -> ps.setString(1, uuid)
        );

        // 4️⃣ JOIN 조회
        List<Map.Entry<String, Long>> entries = jdbcTemplate.query("""
            SELECT g.uuid, g.id
            FROM GAME g
            JOIN temp_uuid t ON g.uuid = t.uuid
        """, (rs, rowNum) ->
                Map.entry(
                        rs.getString("uuid"),
                        rs.getLong("id")
                )
        );

        // 5️⃣ Map 변환
        return entries.stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
    }
}

