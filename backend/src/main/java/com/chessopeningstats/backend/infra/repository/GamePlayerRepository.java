package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    @Query("""
                select max(gp.game.playedAt)
                from GamePlayer gp
                where gp.player.id = :playerId
            """)
    Instant findLatestPlayedAt(long playerId);
}
