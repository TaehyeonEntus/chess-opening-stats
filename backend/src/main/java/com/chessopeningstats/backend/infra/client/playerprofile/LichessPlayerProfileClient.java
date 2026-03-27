package com.chessopeningstats.backend.infra.client.playerprofile;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.ExternalServiceException;
import com.chessopeningstats.backend.exception.RateLimitExceededException;
import com.chessopeningstats.backend.exception.UsernameNotFoundOnPlatformException;
import com.chessopeningstats.backend.infra.client.playerprofile.dto.LichessPlayerProfile;
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
public class LichessPlayerProfileClient implements PlayerProfileClient {
    private final RestClient lichessRestClient;

    @Override
    public Platform platform() {
        return Platform.LICHESS;
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
    public PlayerProfile fetchPlayerProfile(String username) {
        ResponseEntity<LichessPlayerProfile> entity = lichessRestClient.get()
                .uri(uri(username))
                .retrieve()
                .toEntity(LichessPlayerProfile.class);

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
