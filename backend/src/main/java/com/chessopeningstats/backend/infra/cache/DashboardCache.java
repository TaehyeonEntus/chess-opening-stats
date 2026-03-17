package com.chessopeningstats.backend.infra.cache;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class DashboardCache {
    private final RedisTemplate<String, Dashboard> redisTemplate;

    public void cache(Player player, Dashboard dashboard) {
        redisTemplate.opsForValue().set(keyOf(player), dashboard, Duration.ofMinutes(10)); // TTL 적용
    }

    public Dashboard get(Player player) {
        return redisTemplate.opsForValue().get(keyOf(player));
    }

    public boolean contains(Player player) {
        return redisTemplate.hasKey(keyOf(player));
    }

    public String keyOf(Player player) {
        return player.platform() + ":" + player.username();
    }
}