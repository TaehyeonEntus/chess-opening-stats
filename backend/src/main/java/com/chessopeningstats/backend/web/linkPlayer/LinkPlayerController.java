package com.chessopeningstats.backend.web.linkPlayer;

import com.chessopeningstats.backend.application.usecase.linkPlayer.AddPlayerService;
import com.chessopeningstats.backend.application.usecase.linkPlayer.DeletePlayerService;
import com.chessopeningstats.backend.application.usecase.linkPlayer.dto.DeletePlayerRequest;
import com.chessopeningstats.backend.security.AuthService;
import com.chessopeningstats.backend.application.usecase.linkPlayer.dto.AddPlayerRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/player")
@RequiredArgsConstructor
public class LinkPlayerController {
    private final AddPlayerService addPlayerService;
    private final DeletePlayerService deletePlayerService;
    private final AuthService authService;

    @PostMapping("/add")
    public ResponseEntity<?> addPlayer(Authentication authentication, @RequestBody AddPlayerRequest request) {
        addPlayerService.addPlayerOnAccount(authService.getAccountId(authentication), request);
        return ResponseEntity.ok().body("account added successfully");
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deletePlayer(Authentication authentication, @RequestBody DeletePlayerRequest request) {
        deletePlayerService.deletePlayerOnAccount(authService.getAccountId(authentication), request);
        return ResponseEntity.ok().body("account added successfully");
    }
}
