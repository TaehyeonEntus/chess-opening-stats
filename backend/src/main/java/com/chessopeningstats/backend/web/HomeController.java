package com.chessopeningstats.backend.web;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.domain.Player;
import com.chessopeningstats.backend.usecase.ExistsPlayerUseCase;
import com.chessopeningstats.backend.usecase.GetDashboardUseCase;
import com.chessopeningstats.backend.usecase.SyncGameUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HomeController {
    private final GetDashboardUseCase getDashboardUseCase;
    private final ExistsPlayerUseCase existsPlayerUseCase;
    private final SyncGameUseCase syncGameUseCase;

    @GetMapping("/dashboard")
    public ResponseEntity<?> dashboard(@RequestParam Platform platform, @RequestParam String username) {
        return ResponseEntity.ok().body(getDashboardUseCase.getDashboard(Player.of(platform, username)));
    }

    @GetMapping("/player")
    public ResponseEntity<?> exists(@RequestParam Platform platform, @RequestParam String username) {
        return ResponseEntity.ok().body(existsPlayerUseCase.existsPlayer(Player.of(platform, username)));
    }

    @PostMapping("/sync")
    public ResponseEntity<?> sync(@RequestParam Platform platform, @RequestParam String username) {
        syncGameUseCase.syncGame(Player.of(platform, username));
        return ResponseEntity.ok().body(Map.of("message", "sync request success"));
    }
}
