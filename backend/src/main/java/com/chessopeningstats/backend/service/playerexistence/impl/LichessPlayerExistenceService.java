package com.chessopeningstats.backend.service.playerexistence.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerexistence.PlayerExistenceClient;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.LichessPlayerExistence;
import com.chessopeningstats.backend.infra.client.playerexistence.impl.LichessPlayerExistenceClient;
import com.chessopeningstats.backend.service.playerexistence.PlayerExistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LichessPlayerExistenceService implements PlayerExistenceService<LichessPlayerExistence> {
    private final LichessPlayerExistenceClient client;

    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public PlayerExistenceClient<LichessPlayerExistence> client() {
        return this.client;
    }
}
