package com.chessopeningstats.backend.application.stat.dto;

import com.chessopeningstats.backend.domain.GamePlayerColor;

public record Stat(String eco,
                   String epd,
                   String name,
                   GamePlayerColor color,
                   Long wins,
                   Long draws,
                   Long losses) {
}
