package com.chessopeningstats.backend.infra.client.fetchGameClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class FetchGameClientConfig {

    @Value("${api.lichess.base-url}")
    private String lichessBaseUrl;

    @Value("${api.chesscom.base-url}")
    private String chessComBaseUrl;

    private static final int BUFFER_SIZE = 10 * 1024 * 1024;
    private static final ExchangeStrategies bufferStrategy =
            ExchangeStrategies.builder()
                    .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(BUFFER_SIZE))
                    .build();

    @Bean
    public WebClient lichessFetchGameWebClient() {
        return WebClient.builder()
                .baseUrl(lichessBaseUrl)
                .exchangeStrategies(bufferStrategy)
                .defaultHeader("Accept", "application/x-ndjson")
                .build();
    }

    @Bean
    public WebClient chessComFetchGameWebClient() {
        return WebClient.builder()
                .baseUrl(chessComBaseUrl)
                .exchangeStrategies(bufferStrategy)
                .build();
    }
}
