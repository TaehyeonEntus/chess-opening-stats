package com.chessopeningstats.backend.application.usecase.etc.init;

import com.chessopeningstats.backend.application.usecase.etc.loadOpening.OpeningLoadService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InitService {
    private final OpeningLoadService openingLoadService;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        openingLoadService.loadOpening();
    }
}
