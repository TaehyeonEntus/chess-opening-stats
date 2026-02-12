package com.chessopeningstats.backend.infra.client.checkAccountClient.chesscom;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.exception.RemoteApiServerException;
import com.chessopeningstats.backend.infra.client.checkAccountClient.CheckAccountClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ChessComCheckAccountClient implements CheckAccountClient {
    private final WebClient chessComCheckAccountWebClient;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public boolean checkAccount(String username) {
        return chessComCheckAccountWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pub/player/{username}/games/archives")
                        .build(username))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.just(true);
                    } else if (response.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.just(false);
                    } else {
                        return Mono.error(new RemoteApiServerException("Chess.com API 서버 에러: " + response.statusCode()));
                    }
                })
                .blockOptional()
                .orElseThrow(RemoteApiServerException::new);
    }
}
