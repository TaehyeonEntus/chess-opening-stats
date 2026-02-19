package com.chessopeningstats.backend.application.usecase.addPlayer;

import com.chessopeningstats.backend.application.domain.AccountPlayerService;
import com.chessopeningstats.backend.application.domain.AccountService;
import com.chessopeningstats.backend.application.domain.PlayerService;
import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.domain.AccountPlayer;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.infra.client.checkPlayerClient.CheckPlayerClientRegistry;
import com.chessopeningstats.backend.application.usecase.addPlayer.dto.AddPlayerRequest;
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
        Player player = getOrCreatePlayer(request);

        accountPlayerService.saveAccountPlayer(AccountPlayer.of(account, player));
    }

    private Player getOrCreatePlayer(AddPlayerRequest request){
        String username = request.getUsername();
        Platform platform = request.getPlatform();

        if(existsByUsernameAndPlatform(username, platform))
            return getByUsernameAndPlatform(username, platform);
        else if(checkPlayerAvailable(username,platform))
            return playerService.savePlayer(Player.builder()
                    .username(username)
                    .platform(platform)
                    .build());
        else
            throw new PlayerNotFoundException();
    }


    private boolean existsByUsernameAndPlatform(String username, Platform platform){
        return playerService.existsByUsernameAndPlatform(username, platform);
    }

    private Player getByUsernameAndPlatform(String username, Platform platform) {
        return playerService.getPlayerByUsernameAndPlatform(username, platform);
    }

    private boolean checkPlayerAvailable(String username, Platform platform){
        return clientRegistry.getClient(platform).checkPlayer(username);
    }
}
