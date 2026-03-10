package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AccountPlayerRepository extends JpaRepository<AccountPlayer, Long> {
    @Query("""
            select ap.player
            from AccountPlayer ap
            where ap.account.id = :accountId
            """)
    List<Player> findAllPlayersByAccountId(long accountId);

    @Query("""
            select ap.player.id
            from AccountPlayer ap
            where ap.account.id = :accountId
            """)
    List<Long> findAllPlayerIdsByAccountId(long accountId);

    boolean existsByAccountIdAndPlayerId(long accountId, long playerId);

    void deleteByAccountIdAndPlayerId(long accountId, long playerId);
}
