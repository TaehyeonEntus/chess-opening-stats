package com.chessopeningstats.backend.infra.client.playergames;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.ExternalServiceException;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.exception.RateLimitExceededException;
import com.chessopeningstats.backend.infra.client.playergames.dto.LichessRawGame;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class LichessPlayerGamesClient implements PlayerGamesClient<LichessRawGame> {
    private final WebClient lichessWebClient;

    @Override
    public Platform platform() {
        return Platform.LICHESS;
    }

    @Override
    public String uri(Player player) {
        return UriComponentsBuilder
                .fromPath("/api/games/user/{username}")
                .queryParam("pgnInJson", true)
                .queryParam("rated", true)
                .queryParam("perfType", "ultraBullet,bullet,blitz,rapid,classical,correspondence")
                .queryParam("moves", true)
                .build(player.username())
                .toString();
    }

    /**
     * 스트리밍!!!!!!!!
     */
    @Override
    public Flux<LichessRawGame> fetchGames(Player player) {
        return lichessWebClient.get()
                .uri(uri(player))
                .retrieve()
                .bodyToFlux(LichessRawGame.class)
                .retryWhen(retryPolicy())
                .onErrorMap(WebClientResponseException.NotFound.class, PlayerNotFoundException::new)
                .onErrorMap(WebClientResponseException.TooManyRequests.class, RateLimitExceededException::new)
                .onErrorMap(ExternalServiceException::new);
    }
}
