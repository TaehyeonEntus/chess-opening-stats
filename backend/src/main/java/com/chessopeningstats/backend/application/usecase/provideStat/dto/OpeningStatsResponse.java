package com.chessopeningstats.backend.application.usecase.provideStat.dto;

import lombok.Data;

import java.util.List;

@Data(staticConstructor = "of")
public class OpeningStatsResponse {
    private final List<OpeningStat> openingStats;
}
