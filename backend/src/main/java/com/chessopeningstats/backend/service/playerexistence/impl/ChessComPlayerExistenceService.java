package com.chessopeningstats.backend.service.playerexistence.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerexistence.PlayerExistenceClient;
import com.chessopeningstats.backend.infra.client.playerexistence.impl.ChessComPlayerExistenceClient;
import com.chessopeningstats.backend.service.playerexistence.PlayerExistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChessComPlayerExistenceService implements PlayerExistenceService {
    private final ChessComPlayerExistenceClient client;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public PlayerExistenceClient client() {
        return this.client;
    }
}
