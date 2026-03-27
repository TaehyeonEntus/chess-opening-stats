package com.chessopeningstats.backend.infra.client.playergames;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class PlayerGameClientConfig {

    @Value("${api.chesscom.base-url}")
    private String chessComBaseUrl;

    @Value("${api.lichess.base-url}")
    private String lichessBaseUrl;

    private static final int BUFFER_SIZE = 10 * 1024 * 1024;
    private static final ExchangeStrategies bufferStrategy =
            ExchangeStrategies.builder()
                    .codecs(codecs -> codecs.defaultCodecs().maxInMemorySize(BUFFER_SIZE))
                    .build();

    @Bean
    public WebClient chessComWebClient() {
        return WebClient.builder()
                .baseUrl(chessComBaseUrl)
                .exchangeStrategies(bufferStrategy)
                .build();
    }

    @Bean
    public WebClient lichessWebClient() {
        return WebClient.builder()
                .baseUrl(lichessBaseUrl)
                .exchangeStrategies(bufferStrategy)
                .defaultHeader("Accept", "application/x-ndjson")
                .build();
    }
}
