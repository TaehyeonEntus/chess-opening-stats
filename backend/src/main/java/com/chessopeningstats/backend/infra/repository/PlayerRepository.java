package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.web.view.dto.home.ColorRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findById(long id);

    Optional<Player> findByUsernameAndPlatform(String username, Platform platform);

    boolean existsByUsernameAndPlatform(String username, Platform platform);

    @Query("""
            select p.id
            from Player p
            where not exists (
                select 1
                from AccountPlayer ap
                where ap.player = p
            )
            """)
    List<Long> findOrphanPlayerIds();

    @Query("""
            select new com.chessopeningstats.backend.web.view.dto.home.ColorRecord(
                        gp.color,
                        sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.WIN then 1 else 0 end),
                        sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.DRAW then 1 else 0 end),
                        sum(case when gp.result = com.chessopeningstats.backend.domain.GamePlayerResult.LOSE then 1 else 0 end)
            )
            from GamePlayer gp
            join gp.player p
            where p.id in :playerIds
            group by gp.color
            """)
    List<ColorRecord> findRecordsByPlayerIds(List<Long> playerIds);
}
