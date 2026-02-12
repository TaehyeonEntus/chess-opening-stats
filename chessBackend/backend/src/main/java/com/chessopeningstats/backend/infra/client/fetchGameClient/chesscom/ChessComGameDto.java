package com.chessopeningstats.backend.infra.client.fetchGameClient.chesscom;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChessComGameDto {

    private String url;
    private String pgn;
    private String fen;

    private String eco;

    @JsonProperty("time_control")
    private String timeControl;

    @JsonProperty("end_time")
    private long endTime;

    @JsonProperty("time_class")
    private String timeClass;

    private boolean rated;
    private String uuid;

    @JsonProperty("initial_setup")
    private String initialSetup;

    private String rules;
    private String tcn;

    //nullable
    private Map<String, Double> accuracies;

    private PlayerDetail white;
    private PlayerDetail black;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerDetail {
        private String username;
        private String result;
        private int rating;

        @JsonProperty("@id")
        private String id;

        private String uuid;
    }
}
