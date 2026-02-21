package com.chessopeningstats.backend.application.usecase.linkPlayer;

import com.chessopeningstats.backend.application.domain.AccountPlayerService;
import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.application.domain.PlayerService;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerAlreadyLinkedException;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.infra.client.checkPlayerClient.CheckPlayerClientRegistry;
import com.chessopeningstats.backend.application.usecase.linkPlayer.dto.AddPlayerRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddPlayerService {
    private final AccountService accountService;
    private final PlayerService playerService;
    private final AccountPlayerService accountPlayerService;
    private final CheckPlayerClientRegistry clientRegistry;

    @Transactional
    public void addPlayerOnAccount(long accountId, AddPlayerRequest request) {
        Account account = accountService.getAccount(accountId);
        Player player = this.getOrCreatePlayer(request);

        if (accountPlayerService.existsByAccountAndPlayer(account.getId(), player.getId()))
            throw new PlayerAlreadyLinkedException();
        else
            accountPlayerService.saveAccountPlayer(AccountPlayer.of(account, player));
    }

    @Transactional
    public Player getOrCreatePlayer(AddPlayerRequest request) {
        String username = request.getUsername();
        Platform platform = request.getPlatform();

        if (playerService.existsByUsernameAndPlatform(username, platform))
            return playerService.getPlayerByUsernameAndPlatform(username, platform);
        else if (checkPlayerAvailable(username, platform))
            return playerService.savePlayer(Player.builder()
                    .username(username)
                    .platform(platform)
                    .build());
        else
            throw new PlayerNotFoundException();
    }

    private boolean checkPlayerAvailable(String username, Platform platform) {
        return clientRegistry.getClient(platform).checkPlayer(username);
    }
}
