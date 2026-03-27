package com.chessopeningstats.backend.infra.client.playerprofile;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class PlayerProfileClientConfig {
    @Value("${api.lichess.base-url}")
    private String lichessBaseUrl;

    @Value("${api.chesscom.base-url}")
    private String chessComBaseUrl;

    @Bean
    public RestClient lichessRestClient() {
        return RestClient.builder()
                .baseUrl(lichessBaseUrl)
                .build();
    }

    @Bean
    public RestClient chessComRestClient() {
        return RestClient.builder()
                .baseUrl(chessComBaseUrl)
                .build();
    }
}
