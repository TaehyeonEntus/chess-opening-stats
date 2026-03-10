package com.chessopeningstats.backend.service;

import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.repository.AccountPlayerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountPlayerService {
    private final AccountPlayerRepository accountPlayerRepository;
    private final AccountService accountService;
    private final PlayerService playerService;

    public void link(long accountId, long playerId) {
        accountPlayerRepository.save(AccountPlayer.of(accountService.get(accountId), playerService.get(playerId), Instant.now()));
    }

    @Transactional
    public void unlink(long accountId, long playerId) {
        accountPlayerRepository.deleteByAccountIdAndPlayerId(accountId, playerId);
    }

    public boolean isLinked(long accountId, long playerId) {
        return accountPlayerRepository.existsByAccountIdAndPlayerId(accountId, playerId);
    }

    @Transactional
    public void addPlayerAndLink(long accountId, Player player) {
        Player savedPlayer = playerService.save(player);
        link(accountId, savedPlayer.getId());
    }

    public List<Player> getPlayersByAccountId(long accountId) {
        return accountPlayerRepository.findAllPlayersByAccountId(accountId);
    }

    public List<Long> getPlayerIdsByAccountId(long accountId) {
        return accountPlayerRepository.findAllPlayerIdsByAccountId(accountId);
    }
}
