package com.chessopeningstats.backend.infra.cache;

import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {
    @Bean
    public ReactiveRedisTemplate<String, Dashboard> reactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        RedisSerializationContext<String, Dashboard> context = RedisSerializationContext
                .<String, Dashboard>newSerializationContext(new StringRedisSerializer())
                .value(new Jackson2JsonRedisSerializer<>(Dashboard.class))
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
