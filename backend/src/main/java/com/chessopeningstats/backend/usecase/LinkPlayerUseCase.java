package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerAlreadyLinkedException;
import com.chessopeningstats.backend.exception.UsernameNotFoundOnPlatformException;
import com.chessopeningstats.backend.service.AccountPlayerService;
import com.chessopeningstats.backend.service.PlayerService;
import com.chessopeningstats.backend.service.playerexistence.registry.PlayerExistenceServiceRegistry;
import com.chessopeningstats.backend.web.account.dto.LinkPlayerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkPlayerUseCase {
    private final PlayerExistenceServiceRegistry playerExistenceServiceRegistry;
    private final AccountPlayerService accountPlayerService;
    private final PlayerService playerService;

    public void linkPlayerToAccount(long accountId, LinkPlayerRequest request) {
        String username = request.username();
        Platform platform = request.platform();

        if (!playerExistenceServiceRegistry.getService(platform).existsUsername(username))
            throw new UsernameNotFoundOnPlatformException(username, platform);

        if (playerService.existsByUsernameAndPlatform(username, platform)) {
            long playerId = playerService.getByUsernameAndPlatform(username, platform).getId();

            if (accountPlayerService.isLinked(accountId, playerId))
                throw new PlayerAlreadyLinkedException();

            accountPlayerService.link(accountId, playerId);
        }
        else
            accountPlayerService.addPlayerAndLink(accountId, Player.of(username, platform));
    }

}
