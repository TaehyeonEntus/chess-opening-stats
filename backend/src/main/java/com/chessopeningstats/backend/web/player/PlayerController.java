package com.chessopeningstats.backend.web.player;

import com.chessopeningstats.backend.domain.Platform;
import com.chessopeningstats.backend.infra.client.playerprofile.dto.PlayerProfile;
import com.chessopeningstats.backend.service.PlayerProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PlayerController {
    private final PlayerProfileService playerProfileService;

    @GetMapping("/player/profile")
    public PlayerProfile getProfile(@RequestParam Platform platform, @RequestParam String username) {
        return playerProfileService.fetchPlayerProfile(platform, username);
    }
}
