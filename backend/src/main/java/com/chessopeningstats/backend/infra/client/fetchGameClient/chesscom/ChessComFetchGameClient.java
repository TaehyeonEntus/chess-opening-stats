package com.chessopeningstats.backend.infra.client.fetchGameClient.chesscom;

import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.exception.RemoteApiServerException;
import com.chessopeningstats.backend.infra.client.fetchGameClient.FetchGameClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class ChessComFetchGameClient implements FetchGameClient<ChessComGameDto> {

    private final WebClient chessComFetchGameWebClient;

    @Override
    public Flux<ChessComGameDto> fetchGames(Player Player) {
        return getRelevantArchiveUrls(Player.getUsername(), Player.getLastPlayedAt())
                .flatMap(this::fetchGamesFromUrl, 10)  // 동시 10개만 요청
                .flatMapIterable(ChessComArchiveResponse::getGames)
                .filter(dto -> isAfterLastSyncTime(dto, Player.getLastPlayedAt()));
    }

    // 월별 아카이브 Url 가져오기
    private Flux<String> getRelevantArchiveUrls(String username, Instant lastPlayedAt) {
        return chessComFetchGameWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pub/player/{username}/games/archives")
                        .build(username))
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful())
                        return response.bodyToMono(ArchivesResponse.class);
                    else if (response.statusCode().isSameCodeAs(HttpStatus.NOT_FOUND))
                        return Mono.error(PlayerNotFoundException::new);
                    else
                        return Mono.error(RemoteApiServerException::new);
                })
                .flatMapMany(response ->
                        Flux.fromIterable(response.getArchives())
                                .filter(url -> isRelevantArchive(url, lastPlayedAt))
                );
    }

    // url로부터 게임 가져오기
    private Mono<ChessComArchiveResponse> fetchGamesFromUrl(String url) {
        return chessComFetchGameWebClient.get()
                .uri(url)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful())
                        return response.bodyToMono(ChessComArchiveResponse.class);
                    else if (response.statusCode().isSameCodeAs(HttpStatus.NOT_FOUND))
                        return Mono.empty();
                    else
                        return Mono.error(RemoteApiServerException::new);
                });
    }

    // 중복 아카이브 검사
    private boolean isRelevantArchive(String url, Instant lastPlayedAt) {
        try {
            String[] parts = url.split("/");
            int year = Integer.parseInt(parts[parts.length - 2]);
            int month = Integer.parseInt(parts[parts.length - 1]);

            YearMonth urlMonth = YearMonth.of(year, month);
            YearMonth lastPlayedMonth = YearMonth.from(lastPlayedAt.atZone(ZoneId.of("UTC")));
            return !urlMonth.isBefore(lastPlayedMonth);
        } catch (Exception e) {
            return false;
        }
    }

    // 중복 게임 검사
    private boolean isAfterLastSyncTime(ChessComGameDto game, Instant lastSynced) {
        return Instant.ofEpochSecond(game.getEndTime()).isAfter(lastSynced);
    }

    @Data
    static class ArchivesResponse {
        private List<String> archives;
    }

    @Data
    static class ChessComArchiveResponse {
        private List<ChessComGameDto> games = new ArrayList<>();
    }
}
