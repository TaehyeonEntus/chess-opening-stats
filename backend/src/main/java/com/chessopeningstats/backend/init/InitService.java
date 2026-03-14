package com.chessopeningstats.backend.init;

import com.chessopeningstats.backend.infra.cache.OpeningCache;
import com.chessopeningstats.backend.infra.client.opening.OpeningClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitService {
    private final OpeningCache openingCache;
    private final OpeningClient openingClient;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        openingCache.cacheAll(openingClient.fetchOpenings());
    }
}
