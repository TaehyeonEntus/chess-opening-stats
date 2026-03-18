package com.chessopeningstats.backend.infra.cache;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class DashboardCache {
    private final ReactiveRedisTemplate<String, Dashboard> redis;

    //파이프라인만 비동기로 일단 구현하자....
    public Mono<Void> cache(Player player, Dashboard dashboard) {
        return redis.opsForValue().set(keyOf(player), dashboard, Duration.ofMinutes(10)).then();
    }

    //동기
    public Dashboard get(Player player) {
        return redis.opsForValue().get(keyOf(player)).block();
    }

    //동기
    public boolean contains(Player player) {
        return Boolean.TRUE.equals(redis.hasKey(keyOf(player)).block());
    }

    private String keyOf(Player player) {
        return player.platform() + ":" + player.username();
    }
}