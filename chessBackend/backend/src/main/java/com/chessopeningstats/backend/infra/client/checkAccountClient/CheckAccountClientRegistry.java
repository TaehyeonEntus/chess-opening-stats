package com.chessopeningstats.backend.infra.client.checkAccountClient;

import com.chessopeningstats.backend.application.account.AccountCheckService;
import com.chessopeningstats.backend.application.syncGame.internal.provide.GameProvideService;
import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class CheckAccountClientRegistry {

    private final Map<Platform, CheckAccountClient> clientsByPlatform = new EnumMap<>(Platform.class);

    public CheckAccountClientRegistry(List<CheckAccountClient> clients) {
        clients.forEach(client -> clientsByPlatform.put(client.platform(), client));
    }

    public CheckAccountClient getClient(Platform platform) {
        CheckAccountClient client = clientsByPlatform.get(platform);
        if (client == null) {
            throw new UnsupportedPlatformException(platform.name());
        }
        return client;
    }
}
