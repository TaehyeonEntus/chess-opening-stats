package com.chessopeningstats.backend.infra.client.playergames.impl;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.ExternalServiceException;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.exception.RateLimitExceededException;
import com.chessopeningstats.backend.infra.client.playergames.PlayerGameClient;
import com.chessopeningstats.backend.infra.client.playergames.dto.ChessComRawGame;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.ParallelFlux;
import reactor.core.scheduler.Schedulers;

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

    /**
     * 1. 전체 아카이브 가져오기
     * 2. 월별 아카이브 가져오기
     * 3. 월별 아카이브로부터 게임 기록 파싱
     * 4. Flux로 흘려보내기
     */
    @Override
    public ParallelFlux<ChessComRawGame> fetchGames(Player player) {
        return getMonthlyArchiveUrls(player)
                .flatMap(this::fetchMonthlyArchiveGames, FETCH_CONCURRENCY)
                .parallel()
                .runOn(Schedulers.parallel());
    }

    // 월별 아카이브 URL 가져오기
    private Flux<String> getMonthlyArchiveUrls(Player player) {
        return client()
                .get()
                .uri(uri(player))
                .retrieve()
                .bodyToMono(ArchivesResponse.class)
                .flatMapIterable(ArchivesResponse::archives)
                .retryWhen(retryPolicy())
                .onErrorMap(WebClientResponseException.NotFound.class, PlayerNotFoundException::new)
                .onErrorMap(WebClientResponseException.TooManyRequests.class, RateLimitExceededException::new)
                .onErrorMap(ExternalServiceException::new);
    }

    // 해당 월 URL로부터 게임 가져오기 (monthly)
    private Flux<ChessComRawGame> fetchMonthlyArchiveGames(String url) {
        return client()
                .get()
                .uri(url)
                .retrieve()
                .bodyToMono(MonthlyArchiveResponse.class)
                .flatMapIterable(MonthlyArchiveResponse::games)
                .retryWhen(retryPolicy())
                .onErrorMap(WebClientResponseException.NotFound.class, PlayerNotFoundException::new)
                .onErrorMap(WebClientResponseException.TooManyRequests.class, RateLimitExceededException::new)
                .onErrorMap(ExternalServiceException::new);
    }

    // Player의 모든 Archive Url
    private record ArchivesResponse(
            List<String> archives
    ) {
    }

    // 해당 Archive에 포함된 모든 게임
    private record MonthlyArchiveResponse(
            List<ChessComRawGame> games
    ) {
    }
}
