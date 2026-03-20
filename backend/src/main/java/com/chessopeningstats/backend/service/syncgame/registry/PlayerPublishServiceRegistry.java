package com.chessopeningstats.backend.service.syncgame.registry;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import com.chessopeningstats.backend.service.syncgame.PlayerPublishService;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PlayerPublishServiceRegistry {
    private final Map<Platform, PlayerPublishService> servicesByPlatform = new EnumMap<>(Platform.class);

    public PlayerPublishServiceRegistry(List<PlayerPublishService> services) {
        services.forEach(service -> servicesByPlatform.put(service.platform(), service));
    }

    public PlayerPublishService getService(Platform platform) {
        PlayerPublishService service = servicesByPlatform.get(platform);
        if (service == null) {
            throw new UnsupportedPlatformException(platform.name());
        }
        return service;
    }
}
