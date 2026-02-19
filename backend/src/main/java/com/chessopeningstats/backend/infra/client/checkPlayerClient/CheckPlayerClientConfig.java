package com.chessopeningstats.backend.infra.client.checkPlayerClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CheckPlayerClientConfig {

    @Value("${api.lichess.base-url}")
    private String lichessBaseUrl;

    @Value("${api.chesscom.base-url}")
    private String chessComBaseUrl;

    @Bean
    public WebClient lichessCheckPlayerWebClient() {
        return WebClient.builder()
                .baseUrl(lichessBaseUrl)
                .build();
    }

    @Bean
    public WebClient chessComCheckPlayerWebClient() {
        return WebClient.builder()
                .baseUrl(chessComBaseUrl)
                .build();
    }
}
