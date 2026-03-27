package com.chessopeningstats.backend.infra.init;

import com.chessopeningstats.backend.infra.storage.OpeningStorage;
import com.chessopeningstats.backend.infra.client.opening.OpeningsClient;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Initializer {
    private final OpeningStorage openingStorage;
    private final OpeningsClient openingsClient;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        openingStorage.storeAll(openingsClient.fetchOpenings());
    }
}
