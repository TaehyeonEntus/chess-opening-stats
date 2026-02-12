package com.chessopeningstats.backend.web.account;

import com.chessopeningstats.backend.application.player.PlayerService;
import com.chessopeningstats.backend.security.AuthService;
import com.chessopeningstats.backend.web.account.dto.AddAccountRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class AccountController {
    private final PlayerService playerService;
    private final AuthService authService;
    @PostMapping("/add")
    public ResponseEntity<?> addAccount(Authentication authentication, @RequestBody AddAccountRequest request) {
        long playerId = authService.getPlayerIdFromAuthentication(authentication);
        playerService.addAccountOnPlayer(playerId, request);
        return ResponseEntity.ok().body("account added successfully");
    }
}
