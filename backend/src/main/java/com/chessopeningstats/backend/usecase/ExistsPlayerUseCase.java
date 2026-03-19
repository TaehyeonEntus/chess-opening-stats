package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.PlayerExistence;
import com.chessopeningstats.backend.service.playerexistence.registry.PlayerExistenceServiceRegistry;
import com.chessopeningstats.backend.web.dto.PlayerExistenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistsPlayerUseCase {
    private final PlayerExistenceServiceRegistry<PlayerExistence> playerExistenceServiceRegistry;

    public PlayerExistenceResponse existsPlayer(Player player) {
        PlayerExistence playerExistence = playerExistenceServiceRegistry.getService(player.platform()).existsUsername(player.username());
        return new PlayerExistenceResponse(playerExistence.image_url(), playerExistence.last_online());
    }
}
