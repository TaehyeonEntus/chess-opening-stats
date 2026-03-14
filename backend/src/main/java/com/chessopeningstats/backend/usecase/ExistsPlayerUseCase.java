package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.PlayerExistenceDto;
import com.chessopeningstats.backend.service.playerexistence.registry.PlayerExistenceServiceRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistsPlayerUseCase {
    private final PlayerExistenceServiceRegistry playerExistenceServiceRegistry;

    public PlayerExistenceDto existsPlayer(Player player) {
        return playerExistenceServiceRegistry.getService(player.platform()).existsUsername(player.username());
    }
}
