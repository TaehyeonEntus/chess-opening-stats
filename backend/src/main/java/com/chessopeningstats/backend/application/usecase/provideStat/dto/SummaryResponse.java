package com.chessopeningstats.backend.application.usecase.provideStat.dto;

import lombok.Data;

import java.util.List;

@Data(staticConstructor = "of")
public class SummaryResponse {
    private final String nickname;
    private final List<WinRate> winRates;
    private final List<OpeningStat> bestWinRateOpenings;
    private final List<OpeningStat> mostPlayedOpenings;
}
