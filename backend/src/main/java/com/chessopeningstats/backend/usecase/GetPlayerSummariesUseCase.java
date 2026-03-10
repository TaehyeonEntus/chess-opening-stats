package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.service.AccountPlayerService;
import com.chessopeningstats.backend.web.account.dto.PlayerSummary;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPlayerSummariesUseCase {
    private final AccountPlayerService accountPlayerService;

    public List<PlayerSummary> getPlayerSummaries(long accountId) {
        return accountPlayerService.getPlayersByAccountId(accountId).stream()
                .map(player ->
                        new PlayerSummary(
                                player.getId(),
                                player.getUsername(),
                                player.getPlatform(),
                                player.getLastPlayedAt()
                        )
                )
                .toList();
    }
}
