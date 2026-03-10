package com.chessopeningstats.backend.web.view.dto.home;

import com.chessopeningstats.backend.domain.GamePlayerColor;
import com.chessopeningstats.backend.domain.GamePlayerResult;

import java.util.List;

public record GameSummary(
        GamePlayerColor color,
        GamePlayerResult result,
        List<Long> ids
) {
}
