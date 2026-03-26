package com.chessopeningstats.backend.infra.repository;

import com.chessopeningstats.backend.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {
    private final Map<Player, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter save(Player player, SseEmitter emitter) {
        emitters.computeIfAbsent(player, p -> new ArrayList<>()).add(emitter);
        return emitter;
    }

    public void delete(Player player, SseEmitter emitter) {
        emitters.get(player).remove(emitter);
        if (emitters.get(player).isEmpty())
            emitters.remove(player);
    }

    public List<SseEmitter> get(Player player) {
        return emitters.get(player);
    }
}
