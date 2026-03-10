package com.chessopeningstats.backend.web.view.dto.home;

import java.util.List;

public record HomeView(
        AccountSummary account,
        List<PlayerSummary> players,
        ColorDashboard white,
        ColorDashboard black
) {
}