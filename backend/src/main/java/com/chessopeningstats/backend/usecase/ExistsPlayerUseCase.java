package com.chessopeningstats.backend.usecase;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.PlayerExistenceDto;
import com.chessopeningstats.backend.service.playerexistence.registry.PlayerExistenceServiceRegistry;
import com.chessopeningstats.backend.web.dto.PlayerExistenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ExistsPlayerUseCase {
    private final PlayerExistenceServiceRegistry playerExistenceServiceRegistry;

    public PlayerExistenceResponse existsPlayer(Player player) {
        PlayerExistenceDto playerExistenceDto = playerExistenceServiceRegistry.getService(player.platform()).existsUsername(player.username());
        return new PlayerExistenceResponse(playerExistenceDto.getImage_url(), playerExistenceDto.getLast_online());
    }
}
