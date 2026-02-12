package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.PlayerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlayerAccountRepository extends JpaRepository<PlayerAccount, Long> {
    @Query("""
            select pa.account.id
            from PlayerAccount pa
            where pa.player.id = :playerId
            """)
    List<Long> findAccountIdsByPlayerId(long playerId);
}
