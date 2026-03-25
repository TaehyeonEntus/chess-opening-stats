package com.chessopeningstats.backend.infra.client.playerexistence;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.ExternalServiceException;
import com.chessopeningstats.backend.exception.RateLimitExceededException;
import com.chessopeningstats.backend.exception.UsernameNotFoundOnPlatformException;
import com.chessopeningstats.backend.infra.client.playerexistence.dto.PlayerExistence;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;

/**
 * Platform으로부터 유저 유무를 확인할 수 있는 클라이언트
 */
public interface PlayerExistenceClient<T extends PlayerExistence> {
    Platform platform();

    RestClient client();

    String uri(String username);

    Class<T> type();

    default PlayerExistence existsUsername(String username) {
        ResponseEntity<T> entity = client()
                .get()
                .uri(uri(username))
                .retrieve()
                .toEntity(type());

        HttpStatusCode statusCode = entity.getStatusCode();

        return switch (statusCode) {
            case HttpStatus.OK -> entity.getBody();
            case HttpStatus.GONE, HttpStatus.NOT_FOUND -> throw new UsernameNotFoundOnPlatformException(username, platform());
            case HttpStatus.TOO_MANY_REQUESTS ->
                    throw new RateLimitExceededException("Too many requests. Try again later.");
            default -> throw new ExternalServiceException("Unexpected status code: " + statusCode);
        };
    }
}
