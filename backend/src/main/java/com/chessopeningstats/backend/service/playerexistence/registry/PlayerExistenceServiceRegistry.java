package com.chessopeningstats.backend.service.playerexistence.registry;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.PlayerExistence;
import com.chessopeningstats.backend.service.playerexistence.PlayerExistenceService;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class PlayerExistenceServiceRegistry<T extends PlayerExistence> {
    private final Map<Platform, PlayerExistenceService<T>> servicesByPlatform = new EnumMap<>(Platform.class);

    public PlayerExistenceServiceRegistry(List<PlayerExistenceService<T>> services) {
        services.forEach(service -> servicesByPlatform.put(service.platform(), service));
    }

    public PlayerExistenceService<T> getService(Platform platform) {
        PlayerExistenceService<T> service = servicesByPlatform.get(platform);
        if (service == null) {
            throw new UnsupportedPlatformException(platform.name());
        }
        return service;
    }
}
