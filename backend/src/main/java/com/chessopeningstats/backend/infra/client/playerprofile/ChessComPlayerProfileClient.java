package com.chessopeningstats.backend.infra.client.playerprofile;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.ExternalServiceException;
import com.chessopeningstats.backend.exception.RateLimitExceededException;
import com.chessopeningstats.backend.exception.UsernameNotFoundOnPlatformException;
import com.chessopeningstats.backend.infra.client.playerprofile.dto.ChessComPlayerProfile;
import com.chessopeningstats.backend.infra.client.playerprofile.dto.PlayerProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class ChessComPlayerProfileClient implements PlayerProfileClient {
    private final RestClient chessComRestClient;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public String uri(String username) {
        return UriComponentsBuilder
                .fromPath("/pub/player/{username}")
                .build(username)
                .toString();
    }

    @Override
    public PlayerProfile fetchPlayerProfile(String username) {
        ResponseEntity<ChessComPlayerProfile> entity = chessComRestClient.get()
                .uri(uri(username))
                .retrieve()
                .toEntity(ChessComPlayerProfile.class);

        HttpStatusCode statusCode = entity.getStatusCode();

        return switch (statusCode) {
            case HttpStatus.OK -> entity.getBody().toPlayerProfile();
            case HttpStatus.GONE, HttpStatus.NOT_FOUND ->
                    throw new UsernameNotFoundOnPlatformException(username, platform());
            case HttpStatus.TOO_MANY_REQUESTS ->
                    throw new RateLimitExceededException("Too many requests. Try again later.");
            default -> throw new ExternalServiceException("Unexpected status code: " + statusCode);

        };
    }
}
