package com.chessopeningstats.backend.service.syncgame.registry;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import com.chessopeningstats.backend.service.syncgame.GameFetchService;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class GameFetchServiceRegistry<T extends RawGame> {
    private final Map<Platform, GameFetchService<T>> servicesByPlatform = new EnumMap<>(Platform.class);

    public GameFetchServiceRegistry(List<GameFetchService<T>> services) {
        services.forEach(service -> servicesByPlatform.put(service.platform(), service));
    }

    public GameFetchService<T> getService(Platform platform) {
        GameFetchService<T> service = servicesByPlatform.get(platform);
        if (service == null) {
            throw new UnsupportedPlatformException(platform.name());
        }
        return service;
    }
}
