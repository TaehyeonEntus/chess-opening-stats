package com.chessopeningstats.backend.infra.client.playerexistence.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.ExternalServiceException;
import com.chessopeningstats.backend.exception.RateLimitExceededException;
import com.chessopeningstats.backend.exception.UsernameNotFoundOnPlatformException;
import com.chessopeningstats.backend.infra.client.playerexistence.PlayerExistenceClient;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.LichessPlayerExistenceDto;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.PlayerExistenceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class LichessPlayerExistenceClient implements PlayerExistenceClient {
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
    public PlayerExistenceDto existsUsername(String username) {
        ResponseEntity<LichessPlayerExistenceDto> entity = client()
                .get()
                .uri(uri(username))
                .retrieve()
                .toEntity(LichessPlayerExistenceDto.class);

        HttpStatusCode statusCode = entity.getStatusCode();
        LichessPlayerExistenceDto dto = entity.getBody();

        return switch (statusCode) {
            case HttpStatus.OK -> new PlayerExistenceDto(Objects.requireNonNull(dto));
            case HttpStatus.NOT_FOUND -> throw new UsernameNotFoundOnPlatformException(username, platform());
            case HttpStatus.TOO_MANY_REQUESTS ->
                    throw new RateLimitExceededException("Too many requests. Try again later.");
            default -> throw new ExternalServiceException("Unexpected status code: " + statusCode);
        };
    }
}
