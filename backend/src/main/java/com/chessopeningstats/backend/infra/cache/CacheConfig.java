package com.chessopeningstats.backend.infra.cache;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {
    @Bean
    public Cache<Player, Dashboard> dashboardCache() {
        return Caffeine.newBuilder()
                .maximumSize(100)               // 최대 엔트리 수
                .expireAfterWrite(10, TimeUnit.MINUTES) // 쓰기 후 TTL
                .build();
    }
}
