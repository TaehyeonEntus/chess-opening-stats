package com.chessopeningstats.backend.infra.client.playerexistence.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LichessPlayerExistence implements PlayerExistence{
    private Long seenAt;

    @Override
    public String image_url() {
        return null;
    }

    @Override
    public Long last_online() {
        return Duration.of(Instant.now().toEpochMilli() - seenAt, TimeUnit.MILLISECONDS.toChronoUnit()).toSeconds();
    }
}
