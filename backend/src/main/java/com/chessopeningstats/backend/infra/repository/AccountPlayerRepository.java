package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.AccountPlayer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountPlayerRepository extends JpaRepository<AccountPlayer, Long> {
    boolean existsByAccountIdAndPlayerId(long accountId, long playerId);
    void deleteByAccountIdAndPlayerId(long accountId, long playerId);
    long countByPlayerId(long playerId);
}
