package com.chessopeningstats.backend.infra.client.fetchGameClient.lichess;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.exception.RemoteApiServerException;
import com.chessopeningstats.backend.infra.client.fetchGameClient.FetchGameClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class LichessFetchGameClient implements FetchGameClient<LichessGameDto> {

    private final WebClient lichessFetchGameWebClient;

    @Override
    public Flux<LichessGameDto> fetchGames(Player player) {
        return lichessFetchGameWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/games/user/{username}")
                        .queryParam("pgnInJson", true)
                        .queryParam("rated", true)
                        .queryParam("perfType", "ultraBullet,bullet,blitz,rapid,classical,correspondence")
                        .queryParam("max", 2000)
                        .queryParam("moves", true)
                        .queryParam("since", player.getLastPlayedAt().toEpochMilli())
                        .build(player.getUsername())
                )
                .exchangeToFlux(response -> {
                    if (response.statusCode().is2xxSuccessful())
                        return response.bodyToFlux(LichessGameDto.class);
                    else if (response.statusCode().isSameCodeAs(HttpStatus.NOT_FOUND))
                        return Flux.error(PlayerNotFoundException::new);
                    else
                        return Flux.error(RemoteApiServerException::new);
                });
    }
}
