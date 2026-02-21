package com.chessopeningstats.backend.web.gameSync;

import com.chessopeningstats.backend.application.usecase.syncGame.GameSyncFacade;
import com.chessopeningstats.backend.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GameSyncController {
    private final GameSyncFacade gameSyncFacade;
    private final AuthService authService;

    @PostMapping("/sync")
    public ResponseEntity<?> syncAccount(Authentication authentication) {
        gameSyncFacade.sync(authService.getAccountId(authentication));
        return ResponseEntity.ok().body("synchronized successfully");
    }
}
