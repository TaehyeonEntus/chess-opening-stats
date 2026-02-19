package com.chessopeningstats.backend.infra.client.checkPlayerClient;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class CheckPlayerClientRegistry {

    private final Map<Platform, CheckPlayerClient> clientsByPlatform = new EnumMap<>(Platform.class);

    public CheckPlayerClientRegistry(List<CheckPlayerClient> clients) {
        clients.forEach(client -> clientsByPlatform.put(client.platform(), client));
    }

    public CheckPlayerClient getClient(Platform platform) {
        CheckPlayerClient client = clientsByPlatform.get(platform);
        if (client == null) {
            throw new UnsupportedPlatformException(platform.name());
        }
        return client;
    }
}
