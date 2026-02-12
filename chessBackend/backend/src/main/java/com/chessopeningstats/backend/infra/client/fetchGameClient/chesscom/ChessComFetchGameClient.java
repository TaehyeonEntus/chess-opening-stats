package com.chessopeningstats.backend.infra.client.fetchGameClient.chesscom;

import com.chessopeningstats.backend.domain.Account;
import com.chessopeningstats.backend.exception.PlayerNotFoundException;
import com.chessopeningstats.backend.exception.RemoteApiServerException;
import com.chessopeningstats.backend.infra.client.fetchGameClient.FetchGameClient;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChessComFetchGameClient implements FetchGameClient<ChessComGameDto> {

    private final WebClient chessComFetchGameWebClient;

    @Override
    public List<ChessComGameDto> fetchGames(Account account) {
        Instant lastPlayedAt = account.getLastPlayedAt();

        return getRelevantArchiveUrls(account.getUsername(), lastPlayedAt)
                .flatMap(this::fetchGamesFromUrl, 10)  // 동시 10개만 요청
                .flatMapIterable(ChessComArchiveResponse::getGames)
                .filter(dto -> isAfterLastSyncTime(dto, lastPlayedAt))
                .collectList()
                .block();
    }

    // 월별 아카이브 Url 가져오기
    private Flux<String> getRelevantArchiveUrls(String username, Instant lastPlayedAt) {
        return chessComFetchGameWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/pub/player/{username}/games/archives")
                        .build(username))
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        response -> Mono.error(new PlayerNotFoundException("Chess.com에서 플레이어를 찾을 수 없습니다: " + username)))
                .onStatus(httpStatus -> httpStatus.is4xxClientError() || httpStatus.is5xxServerError(),
                        response -> Mono.error(new RemoteApiServerException("Chess.com API 서버 에러: " + response.statusCode())))
                .bodyToMono(ArchivesResponse.class)
                .map(response -> response.getArchives().stream()
                        .filter(url -> isRelevantArchive(url, lastPlayedAt))
                        .collect(Collectors.toList()))
                .flatMapMany(Flux::fromIterable);
    }

    // url로부터 게임 가져오기
    private Mono<ChessComArchiveResponse> fetchGamesFromUrl(String url) {
        return chessComFetchGameWebClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ChessComArchiveResponse.class);
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
            return true;
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
