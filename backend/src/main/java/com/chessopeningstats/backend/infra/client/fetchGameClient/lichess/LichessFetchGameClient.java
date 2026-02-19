package com.chessopeningstats.backend.infra.client.fetchGameClient.lichess;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.exception.RemoteApiServerException;
import com.chessopeningstats.backend.infra.client.fetchGameClient.FetchGameClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LichessFetchGameClient implements FetchGameClient<LichessGameDto> {

    private final WebClient lichessFetchGameWebClient;

    @Override
    public List<LichessGameDto> fetchGames(Player Player) {
        long since = Player.getLastPlayedAt().toEpochMilli();

        return lichessFetchGameWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/games/user/{username}")
                        .queryParam("pgnInJson", true)
                        .queryParam("rated",true)
                        .queryParam("perfType","ultraBullet,bullet,blitz,rapid,classical,correspondence")
                        .queryParam("max", 2000)
                        .queryParam("moves",true)
                        .queryParam("since", since)
                        .build(Player.getUsername())
                )
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        response -> Mono.error(new PlayerNotFoundException("Lichess에서 플레이어를 찾을 수 없습니다: " + Player.getUsername())))
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        response -> Mono.error(new RemoteApiServerException("Lichess API 서버 에러: " + response.statusCode())))
                .bodyToFlux(LichessGameDto.class)
                .collectList()
                .block();
    }
}
