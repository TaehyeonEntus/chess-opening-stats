package com.chessopeningstats.backend.web.gameSync;

import com.chessopeningstats.backend.application.syncGame.GameSyncFacade;
import com.chessopeningstats.backend.security.AuthService;
import com.chessopeningstats.backend.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class GameSyncController {
    private final GameSyncFacade gameSyncFacade;
    private final AuthService authService;
    @GetMapping("/sync")
    public ResponseEntity<?> syncPlayer(Authentication authentication) {
        long playerId = authService.getPlayerIdFromAuthentication(authentication);
        gameSyncFacade.sync(playerId);
        return ResponseEntity.ok().body("synchronized successfully");
    }
}
