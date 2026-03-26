package com.chessopeningstats.backend.web;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.infra.repository.EmitterRepository;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import com.chessopeningstats.backend.usecase.ExistsPlayerUseCase;
import com.chessopeningstats.backend.usecase.SyncPlayerUseCase;
import com.chessopeningstats.backend.web.dto.PlayerExistenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final ExistsPlayerUseCase existsPlayerUseCase;
    private final SyncPlayerUseCase syncPlayerUseCase;
    private final EmitterRepository emitterRepository;
    private final DashboardCache dashboardCache;

    @GetMapping("/player")
    public PlayerExistenceResponse exists(@RequestParam Platform platform, @RequestParam String username) {
        return existsPlayerUseCase.existsPlayer(Player.of(platform, username));
    }

    @GetMapping(value = "/sync", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter sync(@RequestParam Platform platform, @RequestParam String username) throws InterruptedException, IOException {
        SseEmitter emitter = new SseEmitter(600 * 1000L);
        Player player = Player.of(platform, username);
        Dashboard dashboard = dashboardCache.get(player);

        if (dashboard != null) {
            emitter.send(SseEmitter.event().name("dashboard").data(dashboard));
            emitter.complete();
            return emitter;
        }

        emitter.onCompletion(() -> emitterRepository.delete(player, emitter));
        emitter.onTimeout(() -> emitterRepository.delete(player, emitter));
        emitter.onError((e) -> emitterRepository.delete(player, emitter));

        emitterRepository.save(player, emitter);
        syncPlayerUseCase.syncPlayer(player);

        return emitter;
    }
}
