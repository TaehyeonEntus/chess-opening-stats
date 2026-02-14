package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.application.stat.dto.Stat;
import com.chessopeningstats.backend.domain.GamePlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    @Query("""
                select new com.chessopeningstats.backend.application.stat.dto.Stat(
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color,
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.WIN then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.DRAW then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.LOSE then 1 else 0 end)
                )
                from GamePlayer gp
                    join gp.account a
                    join PlayerAccount pa on pa.account = a
                    join gp.game g
                    join g.opening o
                where pa.player.id = :playerId
                group by
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color
            """)
    List<Stat> findAllStatsByPlayerId(long playerId);

    @Query("""
                select new com.chessopeningstats.backend.application.stat.dto.Stat(
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color,
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.WIN then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.DRAW then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.LOSE then 1 else 0 end)
                )
                from GamePlayer gp
                    join gp.game g
                    join g.opening o
                group by
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color
            """)
    List<Stat> findAllStats();
}
