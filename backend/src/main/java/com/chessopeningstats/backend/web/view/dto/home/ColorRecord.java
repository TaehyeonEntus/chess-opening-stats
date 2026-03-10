package com.chessopeningstats.backend.web.view.dto.home;

import com.chessopeningstats.backend.domain.GamePlayerColor;

public record ColorRecord(
        GamePlayerColor color,
        long win,
        long draw,
        long lose
) {
}
