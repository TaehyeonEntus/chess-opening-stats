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

    // 캐시 저장
    public void cache(Player player, Dashboard dashboard) {
        redisTemplate.opsForValue().set(keyOf(player), dashboard, Duration.ofMinutes(60)); // TTL 적용
    }

    // 캐시 조회
    public Dashboard get(Player player) {
        return redisTemplate.opsForValue().get(keyOf(player));
    }

    // 존재 여부 확인
    public boolean contains(Player player) {
        return redisTemplate.hasKey(keyOf(player));
    }

    public String keyOf(Player player) {
        return player.platform() + ":" + player.username();
    }
}