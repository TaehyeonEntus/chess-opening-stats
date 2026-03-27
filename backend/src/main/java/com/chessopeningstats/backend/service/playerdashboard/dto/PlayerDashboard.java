package com.chessopeningstats.backend.service.playerdashboard.dto;

import com.chessopeningstats.backend.domain.Player;

public record PlayerDashboard(
        Player player,
        Dashboard dashboard
) {
}
