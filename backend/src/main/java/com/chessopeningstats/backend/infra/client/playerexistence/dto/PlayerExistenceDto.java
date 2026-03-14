package com.chessopeningstats.backend.infra.client.playerexistence.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerExistenceDto {
    private String image_url;
    private Long last_online;

    public PlayerExistenceDto(ChessComPlayerExistenceDto dto) {
        this.image_url = dto.getAvatar();
        this.last_online = Duration.of(Instant.now().getEpochSecond() - dto.getLast_online(), TimeUnit.SECONDS.toChronoUnit()).toSeconds();
    }

    public PlayerExistenceDto(LichessPlayerExistenceDto dto) {
        this.image_url = null;
        this.last_online = Duration.of(Instant.now().toEpochMilli() - dto.getSeenAt(), TimeUnit.MILLISECONDS.toChronoUnit()).toSeconds();
    }
}
