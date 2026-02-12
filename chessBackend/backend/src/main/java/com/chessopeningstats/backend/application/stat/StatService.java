package com.chessopeningstats.backend.application.stat;

import com.chessopeningstats.backend.application.stat.dto.Stat;
import com.chessopeningstats.backend.application.stat.util.StatUtil;
import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.infra.repository.GamePlayerRepository;
import com.chessopeningstats.backend.infra.repository.PlayerAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatService {
    private final PlayerAccountRepository playerAccountRepository;
    private final GamePlayerRepository gamePlayerRepository;

    public Stat getStatByPlayerId(long playerId) {
        return this.getStatByAccountIds(playerAccountRepository.findAccountIdsByPlayerId(playerId));
    }

    public Stat getStatByAccountIds(Collection<Long> accountIds) {
        return StatUtil.mapToStat(gamePlayerRepository.findStatByAccountIds(accountIds));
    }

    public Stat getOpeningStatByPlayerId(long playerId, List<String> epds) {
        return this.getOpeningStatByAccountIds(playerAccountRepository.findAccountIdsByPlayerId(playerId), epds);
    }

    public Stat getOpeningStatByAccountIds(Collection<Long> accountIds, List<String> epds) {
        return StatUtil.mapToStat(gamePlayerRepository.findOpeningStatByAccountIds(accountIds, epds));
    }
}
