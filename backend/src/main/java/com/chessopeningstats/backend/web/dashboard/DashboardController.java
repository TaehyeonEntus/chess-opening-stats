package com.chessopeningstats.backend.web.dashboard;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.EmitterService;
import com.chessopeningstats.backend.service.PlayerQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class DashboardController {
    private final PlayerQueueService playerQueueService;
    private final EmitterService emitterService;

    @GetMapping(value = "/dashboard", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter getDashboard(@RequestParam Platform platform, @RequestParam String username) {
        Player player = Player.of(platform, username);
        SseEmitter emitter = emitterService.createEmitter(player);

        if (!emitterService.sendDashboardIfExists(player, emitter))
            playerQueueService.enqueuePlayer(player);

        return emitter;
    }
}
