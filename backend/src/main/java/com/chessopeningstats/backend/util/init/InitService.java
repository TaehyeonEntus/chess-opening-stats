package com.chessopeningstats.backend.util.init;

import com.chessopeningstats.backend.util.cacheopening.OpeningCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitService {
    private final OpeningCacheService openingCacheService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        openingCacheService.cacheOpenings();
    }
}
