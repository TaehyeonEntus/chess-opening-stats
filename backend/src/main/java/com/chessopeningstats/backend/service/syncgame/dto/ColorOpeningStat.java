package com.chessopeningstats.backend.service.syncgame.dto;

import com.chessopeningstats.backend.domain.Color;

public record ColorOpeningStat(
        Long openingId,
        Color color,
        long win,
        long draw,
        long lose
) {
}
