package com.chessopeningstats.backend.service.playerexistence;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerexistence.PlayerExistenceClient;

public interface PlayerExistenceService {
    Platform platform();

    PlayerExistenceClient client();

    default boolean existsUsername(String username){
        return client().existsUsername(username);
    }
}
