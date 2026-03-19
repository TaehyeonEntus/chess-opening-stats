package com.chessopeningstats.backend.infra.client.playerexistence.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChessComPlayerExistence implements PlayerExistence{
    private String avatar;
    private Long last_online;

    @Override
    public String image_url() {
        return avatar;
    }

    @Override
    public Long last_online() {
        return Duration.of(Instant.now().getEpochSecond() - last_online, TimeUnit.SECONDS.toChronoUnit()).toSeconds();
    }
}
