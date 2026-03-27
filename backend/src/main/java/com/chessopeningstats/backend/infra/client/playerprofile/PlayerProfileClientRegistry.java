package com.chessopeningstats.backend.infra.client.playerprofile;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PlayerProfileClientRegistry {
    private final Map<Platform, PlayerProfileClient> clientsByPlatform = new EnumMap<>(Platform.class);

    public PlayerProfileClientRegistry(List<PlayerProfileClient> clients) {
        clients.forEach(client -> clientsByPlatform.put(client.platform(), client));
    }

    public PlayerProfileClient getClient(Platform platform) {
        PlayerProfileClient client = clientsByPlatform.get(platform);
        if (client == null) {
            throw new UnsupportedPlatformException(platform.name());
        }
        return client;
    }

}
