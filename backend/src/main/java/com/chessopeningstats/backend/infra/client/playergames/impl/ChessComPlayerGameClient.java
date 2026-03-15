package com.chessopeningstats.backend.infra.client.playergames.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.ExternalServiceException;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.exception.RateLimitExceededException;
import com.chessopeningstats.backend.infra.client.playergames.PlayerGameClient;
import com.chessopeningstats.backend.infra.client.playergames.dto.ChessComRawGame;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChessComPlayerGameClient implements PlayerGameClient<ChessComRawGame> {
    private final WebClient chessComPlayerGameWebClient;
    private static final int FETCH_CONCURRENCY = 100;

    @Override
    public Platform platform() {
        return Platform.CHESS_COM;
    }

    @Override
    public WebClient client() {
        return this.chessComPlayerGameWebClient;
    }

    @Override
    public String uri(Player player) {
        return UriComponentsBuilder
                .fromPath("/pub/player/{username}/games/archives")
                .build(player.username())
                .toString();
    }

    @Override
    public ParallelFlux<ChessComRawGame> fetchGames(Player player) {
        return getArchiveUrls(player)
                .flatMap(this::fetchMonthlyArchiveGames, FETCH_CONCURRENCY)
                .parallel()
                .runOn(Schedulers.parallel())
                .flatMap(archive -> Flux.fromIterable(archive.getGames()));
    }

    private Flux<String> getArchiveUrls(Player player) {
        return client()
                .get()
                .uri(uri(player))
                .retrieve()
                .bodyToMono(ArchivesResponse.class)
                .retryWhen(retryPolicy())
                .onErrorMap(WebClientResponseException.NotFound.class, PlayerNotFoundException::new)
                .onErrorMap(ExternalServiceException::new)
                .flatMapIterable(ArchivesResponse::getArchives);
    }

    // url로부터 게임 가져오기 (monthly)
    private Mono<ChessComArchiveResponse> fetchMonthlyArchiveGames(String url) {
        return client()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(ChessComArchiveResponse.class)
                .retryWhen(retryPolicy())
                .onErrorMap(WebClientResponseException.NotFound.class, PlayerNotFoundException::new)
                .onErrorMap(WebClientResponseException.TooManyRequests.class, RateLimitExceededException::new)
                .onErrorMap(ExternalServiceException::new);
    }

    @Data
    // Player의 모든 Archive Url
    private static class ArchivesResponse {
        private List<String> archives = new ArrayList<>();
    }

    // 해당 Archive에 포함된 모든 게임
    @Data
    private static class ChessComArchiveResponse {
        private List<ChessComRawGame> games = new ArrayList<>();
    }
}
