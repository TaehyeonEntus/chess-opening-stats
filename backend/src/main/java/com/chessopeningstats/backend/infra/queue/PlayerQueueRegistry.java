package com.chessopeningstats.backend.infra.queue;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PlayerQueueRegistry {
    private final Map<Platform, PlayerQueue> queuesByPlatform = new EnumMap<>(Platform.class);

    public PlayerQueueRegistry(List<PlayerQueue> services) {
        services.forEach(service -> queuesByPlatform.put(service.platform(), service));
    }

    public PlayerQueue getQueue(Platform platform) {
        PlayerQueue service = queuesByPlatform.get(platform);
        if (service == null) {
            throw new UnsupportedPlatformException(platform.name());
        }
        return service;
    }
}
