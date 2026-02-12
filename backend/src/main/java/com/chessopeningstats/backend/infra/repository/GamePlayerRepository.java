package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.Game;
import com.chessopeningstats.backend.domain.GamePlayer;
import com.chessopeningstats.backend.domain.GamePlayerColor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface GamePlayerRepository extends JpaRepository<GamePlayer, Long> {
    Optional<GamePlayer> findByGameAndAccount(Game game, Account account);

    @Query("""
                select gp.result, count(gp)
                from GamePlayer gp
                where gp.account.id in :accountIds
                group by gp.result
            """)
    List<Object[]> findStatByAccountIds(Collection<Long> accountIds);

    @Query("""
                select gp.result, count(gp)
                from GamePlayer gp
                    join gp.game g
                    join g.gameOpenings go
                    join go.opening o
                where (gp.account.id in :accountIds) and (o.epd in :epds)
                group by gp.result
            """)
    List<Object[]> findOpeningStatByAccountIds(Collection<Long> accountIds, List<String> epds);
}
