package com.chessopeningstats.backend.application.usecase.linkPlayer;

import com.chessopeningstats.backend.application.domain.AccountPlayerService;
import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.application.domain.PlayerService;
import com.chessopeningstats.backend.application.usecase.linkPlayer.dto.DeletePlayerRequest;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.Player;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeletePlayerService {
    private final AccountService accountService;
    private final PlayerService playerService;
    private final AccountPlayerService accountPlayerService;

    @Transactional
    public void deletePlayerOnAccount(long accountId, DeletePlayerRequest request) {
        Account account = accountService.getAccount(accountId);
        Player player = playerService.getPlayerByUsernameAndPlatform(request.getUsername(), request.getPlatform());

        accountPlayerService.deleteByAccountAndPlayer(account.getId(), player.getId());
    }
}
