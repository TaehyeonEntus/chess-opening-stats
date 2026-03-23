package com.chessopeningstats.backend.web;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.service.syncgame.dto.Dashboard;
import com.chessopeningstats.backend.usecase.ExistsPlayerUseCase;
import com.chessopeningstats.backend.usecase.GetDashboardUseCase;
import com.chessopeningstats.backend.usecase.EnqueuePlayerUseCase;
import com.chessopeningstats.backend.web.dto.PlayerExistenceResponse;
import com.chessopeningstats.backend.web.dto.EnqueuePlayerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final GetDashboardUseCase getDashboardUseCase;
    private final ExistsPlayerUseCase existsPlayerUseCase;
    private final EnqueuePlayerUseCase enqueuePlayerUseCase;

    @GetMapping("/dashboard")
    public Dashboard dashboard(@RequestParam Platform platform, @RequestParam String username) {
        return getDashboardUseCase.getDashboard(Player.of(platform, username));
    }

    @GetMapping("/player")
    public PlayerExistenceResponse exists(@RequestParam Platform platform, @RequestParam String username) {
        return existsPlayerUseCase.existsPlayer(Player.of(platform, username));
    }

    @PostMapping("/sync")
    public EnqueuePlayerResponse sync(@RequestParam Platform platform, @RequestParam String username) throws InterruptedException {
        return enqueuePlayerUseCase.enqueuePlayer(Player.of(platform, username));
    }
}
