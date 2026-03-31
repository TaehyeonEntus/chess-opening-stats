package com.chessopeningstats.backend.web.dashboard;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.playerdashboard.dto.Dashboard;
import com.chessopeningstats.backend.service.playerdashboard.impl.DashboardCacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardCacheService dashboardCacheService;

    @GetMapping("/dashboard")
    public Dashboard getDashboard(@RequestParam Platform platform, @RequestParam String username) {
        return dashboardCacheService.get(Player.of(platform, username));
    }
}
