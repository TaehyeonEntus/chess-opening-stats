package com.chessopeningstats.backend.service.syncgame.registry;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import com.chessopeningstats.backend.infra.client.playergames.dto.RawGame;
import com.chessopeningstats.backend.service.syncgame.GameNormalizeService;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class GameNormalizeServiceRegistry<T extends RawGame> {
    private final Map<Platform, GameNormalizeService<T>> servicesByPlatform = new EnumMap<>(Platform.class);

    public GameNormalizeServiceRegistry(List<GameNormalizeService<T>> services) {
        services.forEach(service -> servicesByPlatform.put(service.platform(), service));
    }

    public GameNormalizeService<T> getService(Platform platform) {
        GameNormalizeService<T> service = servicesByPlatform.get(platform);
        if (service == null) {
            throw new UnsupportedPlatformException(platform.name());
        }
        return service;
    }
}