package com.chessopeningstats.backend.infra.client.playerexistence.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerexistence.PlayerExistenceClient;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.LichessPlayerExistence;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class LichessPlayerExistenceClient implements PlayerExistenceClient<LichessPlayerExistence> {
    private final RestClient lichessPlayerExistenceRestClient;

    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public RestClient client() {
        return this.lichessPlayerExistenceRestClient;
    }

    @Override
    public String uri(String username) {
        return UriComponentsBuilder
                .fromPath("/api/user/{username}")
                .queryParam("profile", false)
                .build(username)
                .toString();
    }

    @Override
    public Class<LichessPlayerExistence> type() {
        return LichessPlayerExistence.class;
    }
}
