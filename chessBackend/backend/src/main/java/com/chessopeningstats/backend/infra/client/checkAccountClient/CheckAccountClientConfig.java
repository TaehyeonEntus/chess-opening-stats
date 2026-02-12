package com.chessopeningstats.backend.infra.client.checkAccountClient;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CheckAccountClientConfig {

    @Value("${api.lichess.base-url}")
    private String lichessBaseUrl;

    @Value("${api.chesscom.base-url}")
    private String chessComBaseUrl;

    @Bean
    public WebClient lichessCheckAccountWebClient() {
        return WebClient.builder()
                .baseUrl(lichessBaseUrl)
                .build();
    }

    @Bean
    public WebClient chessComCheckAccountWebClient() {
        return WebClient.builder()
                .baseUrl(chessComBaseUrl)
                .build();
    }
}
