package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.application.usecase.provideStat.dto.OpeningStat;
import com.chessopeningstats.backend.application.usecase.provideStat.dto.WinRate;
import com.chessopeningstats.backend.domain.GamePlayer;
import com.chessopeningstats.backend.domain.GamePlayerColor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    @Query("""
                select new com.chessopeningstats.backend.application.usecase.provideStat.dto.OpeningStat(
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
                    join GameOpening go on go.game = g
                    join go.opening o
                group by
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color
            """)
    List<OpeningStat> getAllOpeningStats();

    @Query("""
                select new com.chessopeningstats.backend.application.usecase.provideStat.dto.OpeningStat(
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color,
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.WIN then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.DRAW then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.LOSE then 1 else 0 end)
                )
                from GamePlayer gp
                    join gp.player p
                    join AccountPlayer ap on ap.player = p
                    join gp.game g
                    join GameOpening go on go.game = g
                    join go.opening o
                where ap.account.id = :accountId
                group by
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color
            """)
    List<OpeningStat> getAccountOpeningStats(long accountId);

    @Query("""
                select new com.chessopeningstats.backend.application.usecase.provideStat.dto.WinRate(
                    gp.color,
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.WIN then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.DRAW then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.LOSE then 1 else 0 end)
                )
                from GamePlayer gp
                group by gp.color
            """)
    List<WinRate> getAllWinRate();

    @Query("""
                select new com.chessopeningstats.backend.application.usecase.provideStat.dto.WinRate(
                    gp.color,
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.WIN then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.DRAW then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.LOSE then 1 else 0 end)
                )
                from GamePlayer gp
                    join AccountPlayer ap on ap.player = gp.player
                where ap.account.id = :accountId
                group by gp.color
            """)
    List<WinRate> getAccountWinRate(long accountId);


    @Query("""
                select new com.chessopeningstats.backend.application.usecase.provideStat.dto.OpeningStat(
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
                    join GameOpening go on go.game = g
                    join go.opening o
                where gp.color = :color
                group by
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color
                having count(*) >= 30
                order by (sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.WIN then 1.0 else 0.0 end) / count(*)) desc
            """)
    List<OpeningStat> getAllBestWinRateOpeningStats(GamePlayerColor color, Pageable pageable);

    @Query("""
                select new com.chessopeningstats.backend.application.usecase.provideStat.dto.OpeningStat(
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color,
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.WIN then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.DRAW then 1 else 0 end),
                    sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.LOSE then 1 else 0 end)
                )
                from GamePlayer gp
                    join gp.player p
                    join AccountPlayer ap on ap.player = p
                    join gp.game g
                    join GameOpening go on go.game = g
                    join go.opening o
                where gp.color = :color and ap.account.id = :accountId
                group by
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color
                having count(*) >= 30
                order by (sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.WIN then 1.0 else 0.0 end) / count(*)) desc
            """)
    List<OpeningStat> getAccountBestWinRateOpeningStats(long accountId, GamePlayerColor color, Pageable pageable);

    @Query("""
                select new com.chessopeningstats.backend.application.usecase.provideStat.dto.OpeningStat(
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
                    join g.lastMatchedOpening o
                where gp.color = :color
                group by
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color
                order by count(*) desc
            """)
    List<OpeningStat> getAllMostPlayedOpeningStats(GamePlayerColor color, Pageable pageable);

    @Query("""
                select new com.chessopeningstats.backend.application.usecase.provideStat.dto.OpeningStat(
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
                    join g.lastMatchedOpening o
                    join AccountPlayer ap on ap.player = gp.player
                where gp.color = :color and ap.account.id = :accountId
                group by
                    o.eco,
                    o.epd,
                    o.name,
                    gp.color
                order by count(*) desc
            """)
    List<OpeningStat> getAccountMostPlayedOpeningStats(long accountId, GamePlayerColor color, Pageable pageable);
}
