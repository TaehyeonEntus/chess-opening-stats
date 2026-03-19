package com.chessopeningstats.backend.service.playerexistence;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerexistence.PlayerExistenceClient;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.PlayerExistence;

public interface PlayerExistenceService<T extends PlayerExistence> {
    Platform platform();

    PlayerExistenceClient<T> client();

    default PlayerExistence existsUsername(String username) {
        return client().existsUsername(username);
    }
}
