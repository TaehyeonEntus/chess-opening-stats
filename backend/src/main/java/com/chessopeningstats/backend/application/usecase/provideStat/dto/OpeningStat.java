package com.chessopeningstats.backend.application.usecase.provideStat.dto;

import com.chessopeningstats.backend.domain.GamePlayerColor;

public record OpeningStat(String eco,
                          String epd,
                          String name,
                          GamePlayerColor color,
                          Long wins,
                          Long draws,
                          Long losses) {
}
