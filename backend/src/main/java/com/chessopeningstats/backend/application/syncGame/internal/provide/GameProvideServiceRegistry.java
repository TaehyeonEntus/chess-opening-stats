package com.chessopeningstats.backend.application.syncGame.internal.provide;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.UnsupportedPlatformException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class GameProvideServiceRegistry {

    private final Map<Platform, GameProvideService> servicesByPlatform = new EnumMap<>(Platform.class);

    public GameProvideServiceRegistry(List<GameProvideService> services) {
        services.forEach(service -> servicesByPlatform.put(service.platform(), service));
    }

    public GameProvideService getService(Platform platform) {
        GameProvideService service = servicesByPlatform.get(platform);
        if (service == null) {
            throw new UnsupportedPlatformException(platform.name());
        }
        return service;
    }
}
