package com.chessopeningstats.backend.util.cacheopening;

import com.chessopeningstats.backend.infra.cache.OpeningCache;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Transactional
public class OpeningCacheService {
    private final OpeningFetchClient client;
    private final OpeningCache openingCache;

    public void cacheOpenings() {
        openingCache.cacheAll(client.fetchOpenings());
    }
}
