package com.chessopeningstats.backend.infra.client.fetchGameClient.lichess;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LichessGameDto {

    private String id;
    private boolean rated;
    private String variant;
    private String speed;
    private String perf;
    private long createdAt;
    private long lastMoveAt;
    private String status;
    private String source;
    private String winner;

    /**
     * PGN 텍스트: ECO, Opening 정보 등이 여기에 문자열로 포함되어 있습니다.
     * 나중에 이 텍스트를 파싱하여 ECO 등을 추출해야 합니다.
     */
    private String pgn;

    /**
     * 참가자 정보 (White, Black)
     */
    private Players players;

    /**
     * 시간 설정 정보
     */
    private Clock clock;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Players {
        private PlayerDetail white;
        private PlayerDetail black;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlayerDetail {
        private UserInfo user;
        private Integer rating;
        private Integer ratingDiff;
        private Boolean provisional; // 임시 레이팅 여부
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UserInfo {
        private String id;
        private String name;
        private String flair; // "smileys.alien" 같은 아이콘 정보
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Clock {
        private int initial;   // 시작 시간(초)
        private int increment; // 증가분(초)
        private int totalTime; // 전체 시간
    }
}