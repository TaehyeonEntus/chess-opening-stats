package com.chessopeningstats.backend.application.domain;

import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.infra.repository.AccountPlayerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AccountPlayerService {
    private final AccountPlayerRepository accountPlayerRepository;
    public AccountPlayer saveAccountPlayer(AccountPlayer accountPlayer) {
        return accountPlayerRepository.save(accountPlayer);
    }

    @Transactional
    public void deleteByAccountAndPlayer(long accountId, long playerId){
        accountPlayerRepository.deleteByAccountIdAndPlayerId(accountId, playerId);
    }

    public boolean existsByAccountAndPlayer(long accountId, long playerId) {
        return accountPlayerRepository.existsByAccountIdAndPlayerId(accountId, playerId);
    }
}
