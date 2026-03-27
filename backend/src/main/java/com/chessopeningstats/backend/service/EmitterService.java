package com.chessopeningstats.backend.service;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.infra.cache.DashboardCache;
import com.chessopeningstats.backend.infra.repository.EmitterRepository;
import com.chessopeningstats.backend.service.playerdashboard.dto.Dashboard;
import com.chessopeningstats.backend.service.playerdashboard.dto.PlayerDashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class EmitterService {
    private final EmitterRepository emitterRepository;
    private final DashboardCache dashboardCache;

    public SseEmitter createEmitter(Player player) {
        SseEmitter emitter = new SseEmitter(600 * 1000L);

        emitter.onCompletion(() -> emitterRepository.delete(player, emitter));
        emitter.onTimeout(() -> emitterRepository.delete(player, emitter));
        emitter.onError((e) -> emitterRepository.delete(player, emitter));

        return emitterRepository.save(player, emitter);
    }

    public void announce(PlayerDashboard playerDashboard) throws IOException {
        for (SseEmitter emitter : emitterRepository.get(playerDashboard.player())) {
            emitter.send(SseEmitter.event().name("dashboard").data(playerDashboard.dashboard()));
            emitter.complete();
        }
    }

    public boolean sendDashboardIfExists(Player player, SseEmitter emitter){
        Dashboard dashboard = dashboardCache.get(player);

        if (dashboard != null) {
            try {
                emitter.send(SseEmitter.event().name("dashboard").data(dashboard));
            } catch (IOException ignored){
            }
            emitter.complete();
            return true;
        } else{
            return false;
        }
    }
}
