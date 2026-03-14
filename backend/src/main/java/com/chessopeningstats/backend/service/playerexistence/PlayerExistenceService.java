package com.chessopeningstats.backend.service.playerexistence;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerexistence.PlayerExistenceClient;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.PlayerExistenceDto;

public interface PlayerExistenceService {
    Platform platform();

    PlayerExistenceClient client();

    default PlayerExistenceDto existsUsername(String username) {
        return client().existsUsername(username);
    }
}
