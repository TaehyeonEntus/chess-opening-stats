package com.chessopeningstats.backend.web.view.dto.home;

import com.chessopeningstats.backend.domain.GamePlayerColor;

public record ColorOpeningStat(
        GamePlayerColor color,
        Long id,
        long win,
        long draw,
        long lose
) {
}
