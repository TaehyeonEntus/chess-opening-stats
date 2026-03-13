package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.exception.PlayerAlreadyUnlinkedException;
import com.chessopeningstats.backend.service.AccountPlayerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnlinkPlayerUseCase {
    private final AccountPlayerService accountPlayerService;

    public void unlinkPlayerFromAccount(long accountId, long playerId) {
        if (!accountPlayerService.isLinked(accountId, playerId))
            throw new PlayerAlreadyUnlinkedException();

        accountPlayerService.unlink(accountId, playerId);
    }
}
