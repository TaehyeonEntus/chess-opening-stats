package com.chessopeningstats.backend.infra.client.playerexistence.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerexistence.PlayerExistenceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.util.retry.Retry;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class LichessPlayerExistenceClient implements PlayerExistenceClient {
    private final WebClient lichessPlayerExistenceWebClient;

    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public WebClient webClient() {
        return this.lichessPlayerExistenceWebClient;
    }

    @Override
    public String uri(String username) {
        return UriComponentsBuilder
                .fromPath("/api/user/{username}")
                .queryParam("profile", false)
                .build(username)
                .toString();
    }
}
