package com.chessopeningstats.backend.infra.client.playerexistence;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PlayerExistenceClientConfig {
    @Value("${api.lichess.base-url}")
    private String lichessBaseUrl;

    @Value("${api.chesscom.base-url}")
    private String chessComBaseUrl;

    @Bean
    public RestClient lichessPlayerExistenceRestClient() {
        return RestClient.builder()
                .baseUrl(lichessBaseUrl)
                .build();
    }

    @Bean
    public RestClient chessComPlayerExistenceRestClient() {
        return RestClient.builder()
                .baseUrl(chessComBaseUrl)
                .build();
    }
}
