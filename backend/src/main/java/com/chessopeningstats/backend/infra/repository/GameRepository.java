package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("""
            select g.id
            from Game g
            where not exists (
                select 1
                from GamePlayer gp
                where gp.game = g
            )
            """)
    List<Long> findOrphanGameIds();
}
