package com.chessopeningstats.backend.infra.client.playerexistence.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.ExternalServiceException;
import com.chessopeningstats.backend.exception.RateLimitExceededException;
import com.chessopeningstats.backend.exception.UsernameNotFoundOnPlatformException;
import com.chessopeningstats.backend.infra.client.playerexistence.PlayerExistenceClient;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.ChessComPlayerExistenceDto;
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
public class ChessComPlayerExistenceClient implements PlayerExistenceClient {
    private final RestClient chessComPlayerExistenceRestClient;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public RestClient client() {
        return this.chessComPlayerExistenceRestClient;
    }

    @Override
    public String uri(String username) {
        return UriComponentsBuilder
                .fromPath("/pub/player/{username}")
                .build(username)
                .toString();
    }

    @Override
    public PlayerExistenceDto existsUsername(String username) {
        ResponseEntity<ChessComPlayerExistenceDto> entity = client()
                .get()
                .uri(uri(username))
                .retrieve()
                .toEntity(ChessComPlayerExistenceDto.class);

        HttpStatusCode statusCode = entity.getStatusCode();
        ChessComPlayerExistenceDto dto = entity.getBody();

        return switch (statusCode) {
            case HttpStatus.OK ->
                    new PlayerExistenceDto(Objects.requireNonNull(dto));
            case HttpStatus.NOT_FOUND ->
                    throw new UsernameNotFoundOnPlatformException(username, platform());
            case HttpStatus.TOO_MANY_REQUESTS ->
                    throw new RateLimitExceededException("Too many requests. Try again later.");
            default -> throw new ExternalServiceException("Unexpected status code: " + statusCode);
        };
    }
}
