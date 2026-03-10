package com.chessopeningstats.backend.service.playerexistence.registry;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import com.chessopeningstats.backend.service.playerexistence.PlayerExistenceService;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PlayerExistenceServiceRegistry {
    private final Map<Platform, PlayerExistenceService> servicesByPlatform = new EnumMap<>(Platform.class);

    public PlayerExistenceServiceRegistry(List<PlayerExistenceService> services) {
        services.forEach(service -> servicesByPlatform.put(service.platform(), service));
    }

    public PlayerExistenceService getService(Platform platform) {
        PlayerExistenceService service = servicesByPlatform.get(platform);
        if (service == null) {
            throw new UnsupportedPlatformException(platform.name());
        }
        return service;
    }
}
